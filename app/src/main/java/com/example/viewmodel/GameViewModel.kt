package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameDatabase
import com.example.data.PlayerProfileEntity
import com.example.data.PlotEntity
import com.example.data.SeedInventoryEntity
import com.example.data.UpgradeEntity
import com.example.model.GameUpgrade
import com.example.model.GrowthStage
import com.example.model.LeaderboardEntry
import com.example.model.LeaderboardPresets
import com.example.model.PlantCatalog
import com.example.model.PlantRarity
import com.example.model.PlantSpecies
import com.example.model.SeedPack
import com.example.model.SeedPackCatalog
import com.example.model.UpgradeCatalog
import com.example.util.Formatters
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameTab(val label: String, val icon: String) {
    GARDEN("Garden", "🌿"),
    GOLDEN_POT("Golden Pot", "🍯"),
    MERCHANT("Packs", "📦"),
    UPGRADES("Upgrades", "⚡"),
    LEADERBOARD("Tycoons", "🏆")
}

data class PlotUiState(
    val plotIndex: Int,
    val species: PlantSpecies?,
    val stage: GrowthStage,
    val growthProgress: Float,
    val isUnlocked: Boolean,
    val evolutionLevel: Int,
    val currentIncomePerSec: Double,
    val currentSellValue: Double,
    val remainingGrowthSec: Int
)

data class FloatingParticle(
    val id: Long,
    val text: String,
    val xOffsetRatio: Float,
    val yOffsetRatio: Float,
    val isCritical: Boolean = false,
    val colorHex: Long = 0xFFFFD700
)

data class PackOpeningState(
    val pack: SeedPack,
    val seeds: List<PlantSpecies>,
    val revealedIndex: Int = -1
)

data class OfflineReport(
    val offlineSeconds: Long,
    val coinsEarned: Double,
    val workerLevel: Int
)

data class GameUiState(
    val currentTab: GameTab = GameTab.GARDEN,
    val coins: Double = 120.0,
    val lifetimeEarnings: Double = 120.0,
    val goldenPotTaps: Long = 0L,
    val unlockedPlotsCount: Int = 4,
    val totalGardenIncomePerSec: Double = 0.0,
    val plots: List<PlotUiState> = emptyList(),
    val seedInventory: Map<String, Int> = emptyMap(),
    val upgradeLevels: Map<String, Int> = emptyMap(),
    val floatingParticles: List<FloatingParticle> = emptyList(),
    val packOpening: PackOpeningState? = null,
    val offlineReport: OfflineReport? = null,
    val selectedPlotForDetail: PlotUiState? = null,
    val selectedPlotForPlanting: Int? = null,
    val goldenFeverActive: Boolean = false,
    val goldenFeverRemainingSec: Int = 0,
    val goldenFeverCharge: Float = 0f, // 0f to 1f
    val recentEventToast: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = GameDatabase.getDatabase(application)
    private val dao = database.gameDao()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var goldenFeverJob: Job? = null
    private var particleCounter = 0L

    init {
        loadInitialData()
        startGameLoop()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val profile = dao.getPlayerProfile() ?: PlayerProfileEntity(
                coins = 150.0,
                lifetimeEarnings = 150.0,
                unlockedPlotsCount = 4
            )

            val initialPlots = dao.getPlots().ifEmpty {
                // Initialize default 12 plot slots (first 4 unlocked, with 2 starter plants)
                val starterPlots = (0 until 12).map { index ->
                    when (index) {
                        0 -> PlotEntity(
                            plotIndex = 0,
                            speciesId = "mint_sprout",
                            stage = GrowthStage.MATURE.name,
                            growthProgress = 1.0f,
                            plantedAt = System.currentTimeMillis() - 15000,
                            evolutionLevel = 1
                        )
                        1 -> PlotEntity(
                            plotIndex = 1,
                            speciesId = "meadow_daisy",
                            stage = GrowthStage.GROWING.name,
                            growthProgress = 0.6f,
                            plantedAt = System.currentTimeMillis() - 8000,
                            evolutionLevel = 1
                        )
                        else -> PlotEntity(
                            plotIndex = index,
                            speciesId = null,
                            stage = GrowthStage.SPROUT.name,
                            growthProgress = 0f,
                            plantedAt = 0L,
                            evolutionLevel = 1
                        )
                    }
                }
                dao.savePlots(starterPlots)
                starterPlots
            }

            val savedSeeds = dao.getSeedInventory().associate { it.speciesId to it.count }.toMutableMap()
            if (savedSeeds.isEmpty()) {
                // Give initial starter seeds
                savedSeeds["lucky_clover"] = 2
                savedSeeds["sweet_basil"] = 1
                dao.saveSeedInventory(SeedInventoryEntity("lucky_clover", 2))
                dao.saveSeedInventory(SeedInventoryEntity("sweet_basil", 1))
            }

            val savedUpgrades = dao.getUpgrades().associate { it.upgradeId to it.level }

            val unlockedCount = profile.unlockedPlotsCount.coerceIn(4, 12)

            _uiState.update { current ->
                current.copy(
                    coins = profile.coins,
                    lifetimeEarnings = profile.lifetimeEarnings,
                    goldenPotTaps = profile.goldenPotTaps,
                    unlockedPlotsCount = unlockedCount,
                    seedInventory = savedSeeds,
                    upgradeLevels = savedUpgrades,
                    plots = buildPlotUiStates(initialPlots, unlockedCount, savedUpgrades)
                )
            }

            recalculateTotalGardenIncome()

            // Calculate offline earnings if any
            val now = System.currentTimeMillis()
            val offlineSec = ((now - profile.lastActiveTimestamp) / 1000).coerceAtLeast(0)
            val foremanLevel = savedUpgrades["offline_foreman"] ?: 0

            if (offlineSec > 20 && foremanLevel > 0) {
                val maxHours = foremanLevel * 2
                val maxOfflineSec = maxHours * 3600L
                val validOfflineSec = minOf(offlineSec, maxOfflineSec)
                val efficiency = (30 + foremanLevel * 10).coerceAtMost(100) / 100.0
                val totalIncomePerSec = _uiState.value.totalGardenIncomePerSec

                val earned = totalIncomePerSec * validOfflineSec * efficiency
                if (earned > 2.0) {
                    _uiState.update {
                        it.copy(
                            offlineReport = OfflineReport(
                                offlineSeconds = validOfflineSec,
                                coinsEarned = earned,
                                workerLevel = foremanLevel
                            )
                        )
                    }
                }
            }

            // Save updated active timestamp
            saveProfile(now)
        }
    }

    private fun buildPlotUiStates(
        entities: List<PlotEntity>,
        unlockedCount: Int,
        upgrades: Map<String, Int>
    ): List<PlotUiState> {
        val fertilizerLvl = upgrades["mineral_fertilizer"] ?: 0
        val irrigationLvl = upgrades["hydroponic_irrigation"] ?: 0
        val sunlampsLvl = upgrades["sunbeam_lamps"] ?: 0

        val speedMultiplier = 1.0 + (fertilizerLvl * 0.20)
        val incomeMultiplier = 1.0 + (irrigationLvl * 0.25)
        val sellMultiplier = 1.0 + (sunlampsLvl * 0.35)

        val byIndex = entities.associateBy { it.plotIndex }

        return (0 until 12).map { index ->
            val isUnlocked = index < unlockedCount
            val entity = byIndex[index]

            if (!isUnlocked || entity?.speciesId == null) {
                PlotUiState(
                    plotIndex = index,
                    species = null,
                    stage = GrowthStage.SPROUT,
                    growthProgress = 0f,
                    isUnlocked = isUnlocked,
                    evolutionLevel = 1,
                    currentIncomePerSec = 0.0,
                    currentSellValue = 0.0,
                    remainingGrowthSec = 0
                )
            } else {
                val species = PlantCatalog.getById(entity.speciesId)
                val stage = try {
                    GrowthStage.valueOf(entity.stage)
                } catch (_: Exception) {
                    GrowthStage.MATURE
                }
                val evo = entity.evolutionLevel.coerceAtLeast(1)

                val effectiveGrowthDuration = (species.growthDurationSec / speedMultiplier).coerceAtLeast(2.0)
                val remainingSec = if (stage == GrowthStage.MATURE || stage == GrowthStage.EVOLVED) {
                    0
                } else {
                    ((1.0f - entity.growthProgress) * effectiveGrowthDuration).toInt().coerceAtLeast(0)
                }

                val stageRate = when (stage) {
                    GrowthStage.SPROUT -> 0.2
                    GrowthStage.GROWING -> 0.5
                    GrowthStage.MATURE -> 1.0
                    GrowthStage.EVOLVED -> 2.5
                }
                val rate = species.baseIncomePerSec * stageRate * incomeMultiplier * evo

                val stageSellRatio = when (stage) {
                    GrowthStage.SPROUT -> 0.3
                    GrowthStage.GROWING -> 0.7
                    GrowthStage.MATURE -> 1.5
                    GrowthStage.EVOLVED -> 4.0
                }
                val sellVal = species.baseSellValue * stageSellRatio * sellMultiplier * evo

                PlotUiState(
                    plotIndex = index,
                    species = species,
                    stage = stage,
                    growthProgress = entity.growthProgress,
                    isUnlocked = true,
                    evolutionLevel = evo,
                    currentIncomePerSec = rate,
                    currentSellValue = sellVal,
                    remainingGrowthSec = remainingSec
                )
            }
        }
    }

    private fun recalculateTotalGardenIncome() {
        val total = _uiState.value.plots.filter { it.isUnlocked && it.species != null }
            .sumOf { it.currentIncomePerSec }
        _uiState.update { it.copy(totalGardenIncomePerSec = total) }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var tickCount = 0
            while (isActive) {
                delay(500)
                tickCount++

                val state = _uiState.value
                val fertilizerLvl = state.upgradeLevels["mineral_fertilizer"] ?: 0
                val speedMultiplier = 1.0 + (fertilizerLvl * 0.20)
                val gnomeLvl = state.upgradeLevels["gnome_gardener"] ?: 0
                val clockworkLvl = state.upgradeLevels["clockwork_harvester"] ?: 0

                var autoIncomeFromWorkers = 0.0

                // 1. Passive income from plants (per 0.5s tick)
                val halfSecIncome = (state.totalGardenIncomePerSec / 2.0)

                // 2. Advance plant growth
                val updatedPlots = state.plots.map { plot ->
                    if (!plot.isUnlocked || plot.species == null) {
                        plot
                    } else if (plot.stage == GrowthStage.MATURE || plot.stage == GrowthStage.EVOLVED) {
                        plot
                    } else {
                        val duration = (plot.species.growthDurationSec / speedMultiplier).coerceAtLeast(2.0)
                        val increment = (0.5 / duration).toFloat()
                        val newProgress = (plot.growthProgress + increment).coerceAtMost(1.0f)
                        val newStage = if (newProgress >= 1.0f) {
                            GrowthStage.MATURE
                        } else if (newProgress >= 0.4f) {
                            GrowthStage.GROWING
                        } else {
                            GrowthStage.SPROUT
                        }

                        val remainingSec = if (newStage == GrowthStage.MATURE) 0 else {
                            ((1.0f - newProgress) * duration).toInt().coerceAtLeast(0)
                        }

                        plot.copy(
                            growthProgress = newProgress,
                            stage = newStage,
                            remainingGrowthSec = remainingSec
                        )
                    }
                }

                // 3. Automated Gnome worker logic (auto-nurtures growing plants periodically)
                if (gnomeLvl > 0 && tickCount % maxOf(4, 20 - gnomeLvl * 2) == 0) {
                    val growingPlot = updatedPlots.firstOrNull { it.isUnlocked && it.species != null && it.stage != GrowthStage.MATURE && it.stage != GrowthStage.EVOLVED }
                    if (growingPlot != null) {
                        // accelerate by 20%
                        nurturePlot(growingPlot.plotIndex, isWorker = true)
                    }
                }

                // 4. Automated Clockwork Harvester worker logic (auto-collects mature plant bonus)
                if (clockworkLvl > 0 && tickCount % maxOf(6, 24 - clockworkLvl * 2) == 0) {
                    val maturePlots = updatedPlots.filter { it.isUnlocked && it.species != null && (it.stage == GrowthStage.MATURE || it.stage == GrowthStage.EVOLVED) }
                    if (maturePlots.isNotEmpty()) {
                        val target = maturePlots.random()
                        val harvestBonus = target.currentIncomePerSec * 8.0
                        autoIncomeFromWorkers += harvestBonus
                        spawnParticle("+$${Formatters.formatMoney(harvestBonus)} (Worker Harvest)", 0.5f, 0.4f, isCrit = false, color = 0xFF81C784)
                    }
                }

                val totalEarnedThisTick = halfSecIncome + autoIncomeFromWorkers

                _uiState.update { current ->
                    current.copy(
                        coins = current.coins + totalEarnedThisTick,
                        lifetimeEarnings = current.lifetimeEarnings + totalEarnedThisTick,
                        plots = updatedPlots
                    )
                }

                recalculateTotalGardenIncome()

                // Every 10 ticks (5s), save state to DB
                if (tickCount % 10 == 0) {
                    persistGameData()
                }
            }
        }
    }

    fun switchTab(tab: GameTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    // ==========================================
    // GOLDEN POT TAP MECHANIC
    // ==========================================
    fun tapGoldenPot() {
        val state = _uiState.value
        val canLvl = state.upgradeLevels["golden_watering_can"] ?: 0
        val synergyLvl = state.upgradeLevels["golden_dew_synergy"] ?: 0
        val critLvl = state.upgradeLevels["sunburst_crit"] ?: 0

        val baseClick = 1.0 + (canLvl * 2.0) + (canLvl * canLvl * 0.5)
        val synergyClick = state.totalGardenIncomePerSec * (synergyLvl * 0.03)
        var tapValue = baseClick + synergyClick

        val isFever = state.goldenFeverActive
        if (isFever) {
            tapValue *= 3.0
        }

        val critRoll = Random.nextDouble()
        val critChance = (critLvl * 0.05).coerceAtMost(0.60)
        val isCrit = critRoll < critChance
        if (isCrit) {
            tapValue *= 5.0
        }

        val newTaps = state.goldenPotTaps + 1
        val newCharge = if (!isFever) {
            (state.goldenFeverCharge + 0.04f).coerceAtMost(1.0f)
        } else {
            0f
        }

        // Particle offset around center
        val randX = Random.nextFloat() * 0.5f + 0.25f
        val randY = Random.nextFloat() * 0.3f + 0.35f
        val particleText = if (isCrit) "CRIT +${Formatters.formatMoney(tapValue)}" else "+${Formatters.formatMoney(tapValue)}"
        val particleColor = if (isCrit) 0xFFFF5722 else if (isFever) 0xFFFFEB3B else 0xFFFFD700
        spawnParticle(particleText, randX, randY, isCrit, particleColor)

        _uiState.update {
            it.copy(
                coins = it.coins + tapValue,
                lifetimeEarnings = it.lifetimeEarnings + tapValue,
                goldenPotTaps = newTaps,
                goldenFeverCharge = newCharge
            )
        }

        if (newCharge >= 1.0f && !isFever) {
            activateGoldenFever()
        }
    }

    private fun activateGoldenFever() {
        goldenFeverJob?.cancel()
        _uiState.update { it.copy(goldenFeverActive = true, goldenFeverRemainingSec = 20, goldenFeverCharge = 0f) }
        showToast("🌟 GOLDEN FEVER ACTIVATED! 3X TAPS FOR 20s!")

        goldenFeverJob = viewModelScope.launch {
            for (sec in 20 downTo 1) {
                _uiState.update { it.copy(goldenFeverRemainingSec = sec) }
                delay(1000)
            }
            _uiState.update { it.copy(goldenFeverActive = false, goldenFeverRemainingSec = 0) }
        }
    }

    private fun spawnParticle(text: String, x: Float, y: Float, isCrit: Boolean, color: Long) {
        val p = FloatingParticle(
            id = ++particleCounter,
            text = text,
            xOffsetRatio = x,
            yOffsetRatio = y,
            isCritical = isCrit,
            colorHex = color
        )
        _uiState.update {
            val list = (it.floatingParticles + p).takeLast(12)
            it.copy(floatingParticles = list)
        }
    }

    // ==========================================
    // GROW IT OR SELL IT (Garden Plot Actions)
    // ==========================================
    fun nurturePlot(plotIndex: Int, isWorker: Boolean = false) {
        val currentPlot = _uiState.value.plots.getOrNull(plotIndex) ?: return
        if (!currentPlot.isUnlocked || currentPlot.species == null) return

        val costToEvolve = currentPlot.species.baseSellValue * 0.40 * currentPlot.evolutionLevel
        val isMature = currentPlot.stage == GrowthStage.MATURE

        if (isMature) {
            // Evolve to Supercharged / Stage 2
            if (!isWorker && _uiState.value.coins < costToEvolve) {
                showToast("Need ${Formatters.formatMoney(costToEvolve)} to evolve into Supercharged!")
                return
            }

            if (!isWorker) {
                _uiState.update { it.copy(coins = it.coins - costToEvolve) }
            }

            val nextEvo = currentPlot.evolutionLevel + 1
            val updatedPlots = _uiState.value.plots.map { p ->
                if (p.plotIndex == plotIndex) {
                    p.copy(
                        stage = GrowthStage.EVOLVED,
                        evolutionLevel = nextEvo,
                        currentIncomePerSec = currentPlot.species.baseIncomePerSec * 2.5 * nextEvo,
                        currentSellValue = currentPlot.species.baseSellValue * 4.0 * nextEvo
                    )
                } else p
            }
            _uiState.update { it.copy(plots = updatedPlots, selectedPlotForDetail = null) }
            recalculateTotalGardenIncome()
            spawnParticle("EVOLVED! 🌟", 0.5f, 0.45f, isCrit = true, color = 0xFFFFD700)
            showToast("${currentPlot.species.name} evolved to Tier $nextEvo! Income massive boost!")
        } else {
            // Growing stage: nurture accelerates growth by 35%
            val newProgress = (currentPlot.growthProgress + 0.35f).coerceAtMost(1.0f)
            val newStage = if (newProgress >= 1.0f) GrowthStage.MATURE else GrowthStage.GROWING
            val updatedPlots = _uiState.value.plots.map { p ->
                if (p.plotIndex == plotIndex) {
                    p.copy(
                        growthProgress = newProgress,
                        stage = newStage,
                        remainingGrowthSec = if (newStage == GrowthStage.MATURE) 0 else p.remainingGrowthSec / 2
                    )
                } else p
            }
            _uiState.update { it.copy(plots = updatedPlots) }
            spawnParticle(if (isWorker) "Gnome Nurtured! 🧙‍♂️" else "Nurtured! 💧", 0.5f, 0.5f, isCrit = false, color = 0xFF4CAF50)
            if (newStage == GrowthStage.MATURE) {
                showToast("${currentPlot.species.name} is now MATURE and ready to harvest or sell!")
            }
        }
        persistGameData()
    }

    fun sellPlot(plotIndex: Int) {
        val currentPlot = _uiState.value.plots.getOrNull(plotIndex) ?: return
        if (!currentPlot.isUnlocked || currentPlot.species == null) return

        val payout = currentPlot.currentSellValue
        val speciesName = currentPlot.species.name

        // Clear plot
        val updatedPlots = _uiState.value.plots.map { p ->
            if (p.plotIndex == plotIndex) {
                PlotUiState(
                    plotIndex = plotIndex,
                    species = null,
                    stage = GrowthStage.SPROUT,
                    growthProgress = 0f,
                    isUnlocked = true,
                    evolutionLevel = 1,
                    currentIncomePerSec = 0.0,
                    currentSellValue = 0.0,
                    remainingGrowthSec = 0
                )
            } else p
        }

        spawnParticle("+$${Formatters.formatMoney(payout)} SOLD!", 0.5f, 0.45f, isCrit = true, color = 0xFFFFC107)

        _uiState.update {
            it.copy(
                coins = it.coins + payout,
                lifetimeEarnings = it.lifetimeEarnings + payout,
                plots = updatedPlots,
                selectedPlotForDetail = null
            )
        }

        recalculateTotalGardenIncome()
        showToast("Sold $speciesName for +$${Formatters.formatMoney(payout)}!")
        persistGameData()
    }

    fun openPlantDialog(plot: PlotUiState) {
        _uiState.update { it.copy(selectedPlotForDetail = plot) }
    }

    fun closePlantDialog() {
        _uiState.update { it.copy(selectedPlotForDetail = null) }
    }

    fun selectPlotForPlanting(plotIndex: Int) {
        _uiState.update { it.copy(selectedPlotForPlanting = plotIndex) }
    }

    fun dismissPlantingModal() {
        _uiState.update { it.copy(selectedPlotForPlanting = null) }
    }

    fun plantSeed(plotIndex: Int, speciesId: String) {
        val count = _uiState.value.seedInventory[speciesId] ?: 0
        if (count <= 0) {
            showToast("No seeds of that species available!")
            return
        }

        val species = PlantCatalog.getById(speciesId)
        val updatedSeeds = _uiState.value.seedInventory.toMutableMap()
        if (count == 1) {
            updatedSeeds.remove(speciesId)
        } else {
            updatedSeeds[speciesId] = count - 1
        }

        val updatedPlots = _uiState.value.plots.map { p ->
            if (p.plotIndex == plotIndex) {
                PlotUiState(
                    plotIndex = plotIndex,
                    species = species,
                    stage = GrowthStage.SPROUT,
                    growthProgress = 0.05f,
                    isUnlocked = true,
                    evolutionLevel = 1,
                    currentIncomePerSec = species.baseIncomePerSec * 0.2,
                    currentSellValue = species.baseSellValue * 0.3,
                    remainingGrowthSec = species.growthDurationSec
                )
            } else p
        }

        _uiState.update {
            it.copy(
                plots = updatedPlots,
                seedInventory = updatedSeeds,
                selectedPlotForPlanting = null
            )
        }

        recalculateTotalGardenIncome()
        showToast("Planted ${species.name}! Water and grow for maximum profits.")
        persistGameData()
    }

    // ==========================================
    // PLOT EXPANSION
    // ==========================================
    fun getNextPlotCost(): Double {
        val current = _uiState.value.unlockedPlotsCount
        return 100.0 * Math.pow(2.8, (current - 4).toDouble())
    }

    fun unlockNextPlot() {
        val current = _uiState.value.unlockedPlotsCount
        if (current >= 12) {
            showToast("Garden is at maximum capacity (12 plots)!")
            return
        }

        val cost = getNextPlotCost()
        if (_uiState.value.coins < cost) {
            showToast("Need ${Formatters.formatMoney(cost)} to expand garden!")
            return
        }

        val nextCount = current + 1
        val updatedPlots = _uiState.value.plots.map { p ->
            if (p.plotIndex == current) {
                p.copy(isUnlocked = true)
            } else p
        }

        _uiState.update {
            it.copy(
                coins = it.coins - cost,
                unlockedPlotsCount = nextCount,
                plots = updatedPlots
            )
        }

        showToast("New Garden Plot Unlocked! Plot #$nextCount ready for rare seeds.")
        persistGameData()
    }

    // ==========================================
    // MERCHANT & SEED PACKS (RNG GACHA)
    // ==========================================
    fun buySeedPack(pack: SeedPack) {
        if (_uiState.value.coins < pack.cost) {
            showToast("Need ${Formatters.formatMoney(pack.cost)} to buy ${pack.name}!")
            return
        }

        val seedsRolled = SeedPackCatalog.openPack(pack)

        // Add to inventory
        val updatedSeeds = _uiState.value.seedInventory.toMutableMap()
        seedsRolled.forEach { sp ->
            updatedSeeds[sp.id] = (updatedSeeds[sp.id] ?: 0) + 1
        }

        _uiState.update {
            it.copy(
                coins = it.coins - pack.cost,
                seedInventory = updatedSeeds,
                packOpening = PackOpeningState(pack = pack, seeds = seedsRolled, revealedIndex = 0)
            )
        }

        persistGameData()
    }

    fun revealNextPackCard() {
        val current = _uiState.value.packOpening ?: return
        val nextIdx = current.revealedIndex + 1
        if (nextIdx < current.seeds.size) {
            _uiState.update { it.copy(packOpening = current.copy(revealedIndex = nextIdx)) }
        }
    }

    fun dismissPackOpening() {
        _uiState.update { it.copy(packOpening = null) }
    }

    // ==========================================
    // UPGRADES & WORKERS
    // ==========================================
    fun buyUpgrade(upgrade: GameUpgrade) {
        val currentLvl = _uiState.value.upgradeLevels[upgrade.id] ?: 0
        if (currentLvl >= upgrade.maxLevel) {
            showToast("${upgrade.name} is already at MAX level!")
            return
        }

        val cost = UpgradeCatalog.getCost(upgrade, currentLvl)
        if (_uiState.value.coins < cost) {
            showToast("Need ${Formatters.formatMoney(cost)} for ${upgrade.name}!")
            return
        }

        val updatedUpgrades = _uiState.value.upgradeLevels.toMutableMap()
        updatedUpgrades[upgrade.id] = currentLvl + 1

        _uiState.update {
            it.copy(
                coins = it.coins - cost,
                upgradeLevels = updatedUpgrades
            )
        }

        // Re-apply upgrade multipliers to plots
        val recomputedPlots = buildPlotUiStates(
            _uiState.value.plots.map { p ->
                PlotEntity(
                    plotIndex = p.plotIndex,
                    speciesId = p.species?.id,
                    stage = p.stage.name,
                    growthProgress = p.growthProgress,
                    plantedAt = 0L,
                    evolutionLevel = p.evolutionLevel
                )
            },
            _uiState.value.unlockedPlotsCount,
            updatedUpgrades
        )

        _uiState.update { it.copy(plots = recomputedPlots) }
        recalculateTotalGardenIncome()
        showToast("Upgraded ${upgrade.name} to Level ${currentLvl + 1}!")
        persistGameData()
    }

    // ==========================================
    // OFFLINE REPORT CLAIM
    // ==========================================
    fun claimOfflineEarnings(doubleMultiplier: Boolean = false) {
        val report = _uiState.value.offlineReport ?: return
        val multiplier = if (doubleMultiplier) 2.0 else 1.0
        val finalCoins = report.coinsEarned * multiplier

        _uiState.update {
            it.copy(
                coins = it.coins + finalCoins,
                lifetimeEarnings = it.lifetimeEarnings + finalCoins,
                offlineReport = null
            )
        }

        showToast("Claimed +$${Formatters.formatMoney(finalCoins)} from your offline workers!")
        persistGameData()
    }

    // ==========================================
    // LEADERBOARD COMPUTATION
    // ==========================================
    fun getFullLeaderboard(): List<LeaderboardEntry> {
        val state = _uiState.value
        val playerEntry = LeaderboardEntry(
            rank = 1,
            name = "You (${getPlayerTitle(state.lifetimeEarnings)})",
            title = getPlayerTitle(state.lifetimeEarnings),
            netWorth = state.lifetimeEarnings,
            gardenIncomePerSec = state.totalGardenIncomePerSec,
            avatarEmoji = "🧑‍🌾",
            isPlayer = true,
            badge = "You"
        )

        val all = (LeaderboardPresets.rivals + playerEntry).sortedByDescending { it.netWorth }
        return all.mapIndexed { idx, entry ->
            entry.copy(rank = idx + 1)
        }
    }

    private fun getPlayerTitle(earnings: Double): String {
        return when {
            earnings >= 100_000_000.0 -> "Botanical Overlord"
            earnings >= 10_000_000.0 -> "Mythic Flora Mogul"
            earnings >= 1_000_000.0 -> "Greenhouse Billionaire"
            earnings >= 100_000.0 -> "High-Stakes Cultivator"
            earnings >= 10_000.0 -> "Master Planter"
            earnings >= 1_000.0 -> "Sprout Enthusiast"
            else -> "Fledgling Seedling"
        }
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(recentEventToast = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(recentEventToast = null) }
    }

    // ==========================================
    // PERSISTENCE
    // ==========================================
    private fun persistGameData() {
        viewModelScope.launch {
            val state = _uiState.value
            saveProfile(System.currentTimeMillis())

            val plotEntities = state.plots.map { p ->
                PlotEntity(
                    plotIndex = p.plotIndex,
                    speciesId = p.species?.id,
                    stage = p.stage.name,
                    growthProgress = p.growthProgress,
                    plantedAt = 0L,
                    evolutionLevel = p.evolutionLevel
                )
            }
            dao.savePlots(plotEntities)

            state.seedInventory.forEach { (speciesId, count) ->
                dao.saveSeedInventory(SeedInventoryEntity(speciesId, count))
            }
            dao.cleanupEmptySeeds()

            state.upgradeLevels.forEach { (upId, level) ->
                dao.saveUpgrade(UpgradeEntity(upId, level))
            }
        }
    }

    private suspend fun saveProfile(timestamp: Long) {
        val state = _uiState.value
        dao.savePlayerProfile(
            PlayerProfileEntity(
                id = 1,
                coins = state.coins,
                lifetimeEarnings = state.lifetimeEarnings,
                goldenPotTaps = state.goldenPotTaps,
                lastActiveTimestamp = timestamp,
                unlockedPlotsCount = state.unlockedPlotsCount
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        goldenFeverJob?.cancel()
    }
}
