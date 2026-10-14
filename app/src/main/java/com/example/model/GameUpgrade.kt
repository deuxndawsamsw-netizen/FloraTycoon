package com.example.model

enum class UpgradeCategory {
    GROWTH,
    WORKERS_AUTOMATION,
    GOLDEN_POT
}

data class GameUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val category: UpgradeCategory,
    val baseCost: Double,
    val costMultiplier: Double,
    val maxLevel: Int,
    val iconEmoji: String,
    val effectDescription: (level: Int) -> String
)

object UpgradeCatalog {
    val upgrades: List<GameUpgrade> = listOf(
        // Growth Upgrades
        GameUpgrade(
            id = "mineral_fertilizer",
            name = "Bio-Mineral Fertilizer",
            description = "Infuses root systems with mineral energy, speeding up plant growth.",
            category = UpgradeCategory.GROWTH,
            baseCost = 75.0,
            costMultiplier = 1.6,
            maxLevel = 15,
            iconEmoji = "🧪",
            effectDescription = { level -> "+${level * 20}% faster plant growth speed" }
        ),
        GameUpgrade(
            id = "hydroponic_irrigation",
            name = "Hydroponic Sprinklers",
            description = "High-pressure nutrient mist that boosts all plants' passive $/sec revenue.",
            category = UpgradeCategory.GROWTH,
            baseCost = 250.0,
            costMultiplier = 1.7,
            maxLevel = 20,
            iconEmoji = "💧",
            effectDescription = { level -> "+${level * 25}% to all garden plant revenue" }
        ),
        GameUpgrade(
            id = "sunbeam_lamps",
            name = "Full-Spectrum Sunlamps",
            description = "Simulates perpetual tropical sunlight for enhanced photosynthesis.",
            category = UpgradeCategory.GROWTH,
            baseCost = 1200.0,
            costMultiplier = 1.8,
            maxLevel = 10,
            iconEmoji = "☀️",
            effectDescription = { level -> "+${level * 35}% sell value when harvesting plants" }
        ),

        // Workers & Automation
        GameUpgrade(
            id = "gnome_gardener",
            name = "Gnome Field Assistant",
            description = "Hires diligent botanical gnomes to tend crops and harvest mature plants.",
            category = UpgradeCategory.WORKERS_AUTOMATION,
            baseCost = 150.0,
            costMultiplier = 2.0,
            maxLevel = 10,
            iconEmoji = "🧙‍♂️",
            effectDescription = { level ->
                if (level == 0) "Not hired"
                else "Auto-nurtures growing crops every ${maxOf(2, 10 - level)}s"
            }
        ),
        GameUpgrade(
            id = "clockwork_harvester",
            name = "Clockwork Auto-Harvester",
            description = "Brass automaton that auto-harvests mature plants when plots are full.",
            category = UpgradeCategory.WORKERS_AUTOMATION,
            baseCost = 1000.0,
            costMultiplier = 2.2,
            maxLevel = 8,
            iconEmoji = "⚙️",
            effectDescription = { level ->
                if (level == 0) "Not active"
                else "Auto-harvests mature plants & banks bonus cash every ${maxOf(3, 12 - level)}s"
            }
        ),
        GameUpgrade(
            id = "offline_foreman",
            name = "Foreman Barnaby's Shift",
            description = "Enables workers to continue harvesting and banking profits while you are offline!",
            category = UpgradeCategory.WORKERS_AUTOMATION,
            baseCost = 500.0,
            costMultiplier = 1.9,
            maxLevel = 12,
            iconEmoji = "🦡",
            effectDescription = { level ->
                if (level == 0) "Offline harvesting locked"
                else "Harvests up to ${level * 2} hours offline at ${minOf(100, 30 + level * 10)}% efficiency"
            }
        ),

        // Golden Pot Upgrades
        GameUpgrade(
            id = "golden_watering_can",
            name = "Gilded Watering Can",
            description = "Increases direct money produced with each tap on the Golden Pot.",
            category = UpgradeCategory.GOLDEN_POT,
            baseCost = 25.0,
            costMultiplier = 1.45,
            maxLevel = 30,
            iconEmoji = "🫖",
            effectDescription = { level -> "+$${level * 2 + (level * level * 0.5).toInt()} per tap" }
        ),
        GameUpgrade(
            id = "golden_dew_synergy",
            name = "Golden Dew Synergy",
            description = "Channels your entire garden's passive $/sec directly into each Golden Pot tap!",
            category = UpgradeCategory.GOLDEN_POT,
            baseCost = 800.0,
            costMultiplier = 2.0,
            maxLevel = 10,
            iconEmoji = "🍯",
            effectDescription = { level -> "+${level * 3}% of total Garden $/sec per tap" }
        ),
        GameUpgrade(
            id = "sunburst_crit",
            name = "Sunburst Alchemy",
            description = "Chance for Golden Pot clicks to trigger a 5x Golden Critical Burst!",
            category = UpgradeCategory.GOLDEN_POT,
            baseCost = 400.0,
            costMultiplier = 1.75,
            maxLevel = 10,
            iconEmoji = "💥",
            effectDescription = { level -> "${level * 5}% chance for 5x Critical Coin Burst" }
        )
    )

    fun getCost(upgrade: GameUpgrade, currentLevel: Int): Double {
        return upgrade.baseCost * Math.pow(upgrade.costMultiplier, currentLevel.toDouble())
    }
}
