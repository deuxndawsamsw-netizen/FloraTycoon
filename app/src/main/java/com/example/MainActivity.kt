package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.PrecisionManufacturing
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.OfflineEarningsDialog
import com.example.ui.components.PackOpeningDialog
import com.example.ui.components.PlantDetailDialog
import com.example.ui.components.PlantSeedModal
import com.example.ui.components.TycoonHeader
import com.example.ui.screens.GardenScreen
import com.example.ui.screens.GoldenPotScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MerchantScreen
import com.example.ui.screens.UpgradesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameTab
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FloraTycoonApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FloraTycoonApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    // Display event toasts via Snackbar
    LaunchedEffect(uiState.recentEventToast) {
        uiState.recentEventToast?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearToast()
        }
    }

    // Android back navigation handler: if user is on any other tab, pressing back returns to Garden
    BackHandler(enabled = uiState.currentTab != GameTab.GARDEN) {
        viewModel.switchTab(GameTab.GARDEN)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val tabs = listOf(
                    Triple(GameTab.GARDEN, Icons.Filled.Grass, Icons.Outlined.Grass),
                    Triple(GameTab.GOLDEN_POT, Icons.Filled.Bolt, Icons.Outlined.Bolt),
                    Triple(GameTab.MERCHANT, Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
                    Triple(GameTab.UPGRADES, Icons.Filled.PrecisionManufacturing, Icons.Outlined.PrecisionManufacturing),
                    Triple(GameTab.LEADERBOARD, Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
                )

                tabs.forEach { (tab, selectedIcon, unselectedIcon) ->
                    val isSelected = uiState.currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.switchTab(tab)
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tycoon Top Balance & Revenue Header
            TycoonHeader(
                coins = uiState.coins,
                gardenIncomePerSec = uiState.totalGardenIncomePerSec,
                goldenFeverActive = uiState.goldenFeverActive,
                goldenFeverSec = uiState.goldenFeverRemainingSec
            )

            // Primary Screen Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState.currentTab) {
                    GameTab.GARDEN -> {
                        GardenScreen(
                            plots = uiState.plots,
                            unlockedPlotsCount = uiState.unlockedPlotsCount,
                            nextPlotCost = viewModel.getNextPlotCost(),
                            playerCoins = uiState.coins,
                            onPlotClick = { plot ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.openPlantDialog(plot)
                            },
                            onEmptyPlotClick = { plotIndex ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.selectPlotForPlanting(plotIndex)
                            },
                            onUnlockNextPlot = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.unlockNextPlot()
                            },
                            onNurturePlot = { plotIndex ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.nurturePlot(plotIndex)
                            },
                            onSellPlot = { plotIndex ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.sellPlot(plotIndex)
                            }
                        )
                    }

                    GameTab.GOLDEN_POT -> {
                        GoldenPotScreen(
                            coins = uiState.coins,
                            gardenIncomePerSec = uiState.totalGardenIncomePerSec,
                            goldenPotTaps = uiState.goldenPotTaps,
                            goldenFeverActive = uiState.goldenFeverActive,
                            goldenFeverRemainingSec = uiState.goldenFeverRemainingSec,
                            goldenFeverCharge = uiState.goldenFeverCharge,
                            upgradeLevels = uiState.upgradeLevels,
                            floatingParticles = uiState.floatingParticles,
                            onTapGoldenPot = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.tapGoldenPot()
                            },
                            onBuyUpgrade = { upgrade ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.buyUpgrade(upgrade)
                            }
                        )
                    }

                    GameTab.MERCHANT -> {
                        MerchantScreen(
                            coins = uiState.coins,
                            seedInventory = uiState.seedInventory,
                            onBuySeedPack = { pack ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.buySeedPack(pack)
                            },
                            onNavigateToGarden = {
                                viewModel.switchTab(GameTab.GARDEN)
                            }
                        )
                    }

                    GameTab.UPGRADES -> {
                        UpgradesScreen(
                            coins = uiState.coins,
                            upgradeLevels = uiState.upgradeLevels,
                            onBuyUpgrade = { upgrade ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.buyUpgrade(upgrade)
                            }
                        )
                    }

                    GameTab.LEADERBOARD -> {
                        LeaderboardScreen(
                            leaderboardEntries = viewModel.getFullLeaderboard(),
                            lifetimeEarnings = uiState.lifetimeEarnings,
                            gardenIncomePerSec = uiState.totalGardenIncomePerSec,
                            goldenPotTaps = uiState.goldenPotTaps,
                            unlockedPlotsCount = uiState.unlockedPlotsCount
                        )
                    }
                }
            }
        }
    }

    // Modal: Plant Seed from Inventory
    uiState.selectedPlotForPlanting?.let { plotIndex ->
        PlantSeedModal(
            plotIndex = plotIndex,
            seedInventory = uiState.seedInventory,
            onPlantSeed = { targetPlot, speciesId ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.plantSeed(targetPlot, speciesId)
            },
            onNavigateToMerchant = {
                viewModel.dismissPlantingModal()
                viewModel.switchTab(GameTab.MERCHANT)
            },
            onDismiss = {
                viewModel.dismissPlantingModal()
            }
        )
    }

    // Modal: Plant Detail Inspection (Grow it or Sell it)
    uiState.selectedPlotForDetail?.let { plot ->
        PlantDetailDialog(
            plot = plot,
            playerCoins = uiState.coins,
            onNurture = { plotIndex ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.nurturePlot(plotIndex)
            },
            onSell = { plotIndex ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.sellPlot(plotIndex)
            },
            onDismiss = {
                viewModel.closePlantDialog()
            }
        )
    }

    // Modal: Gacha Mystery Seed Pack Opening
    uiState.packOpening?.let { packOpeningState ->
        PackOpeningDialog(
            packState = packOpeningState,
            onRevealNext = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.revealNextPackCard()
            },
            onCollectAll = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.dismissPackOpening()
            }
        )
    }

    // Modal: Offline Earnings Welcome Back Report
    uiState.offlineReport?.let { report ->
        OfflineEarningsDialog(
            report = report,
            onCollectNormal = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.claimOfflineEarnings(doubleMultiplier = false)
            },
            onCollectDouble = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.claimOfflineEarnings(doubleMultiplier = true)
            }
        )
    }
}
