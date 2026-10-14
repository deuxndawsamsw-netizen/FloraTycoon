package com.example.model

enum class PlantRarity(val displayName: String, val colorHex: Long, val weight: Int) {
    COMMON("Common", 0xFF4CAF50, 60),
    UNCOMMON("Uncommon", 0xFF0288D1, 25),
    RARE("Rare", 0xFF8E24AA, 10),
    EPIC("Epic", 0xFFE65100, 4),
    LEGENDARY("Legendary", 0xFFFFB300, 1)
}

enum class GrowthStage {
    SPROUT,     // 0% - 39%
    GROWING,    // 40% - 99%
    MATURE,     // 100% Ready to sell or evolve
    EVOLVED     // Supercharged prestige stage
}

data class PlantSpecies(
    val id: String,
    val name: String,
    val botanicalName: String,
    val rarity: PlantRarity,
    val baseIncomePerSec: Double,
    val baseSellValue: Double,
    val growthDurationSec: Int,
    val description: String,
    val iconEmoji: String,
    val primaryColorHex: Long
)

object PlantCatalog {
    val allSpecies: List<PlantSpecies> = listOf(
        // Common
        PlantSpecies(
            id = "mint_sprout",
            name = "Peppermint Sprout",
            botanicalName = "Mentha piperita",
            rarity = PlantRarity.COMMON,
            baseIncomePerSec = 1.0,
            baseSellValue = 35.0,
            growthDurationSec = 8,
            description = "A brisk, fragrant herb that sprouts vigorously in any soil.",
            iconEmoji = "🌿",
            primaryColorHex = 0xFF4CAF50
        ),
        PlantSpecies(
            id = "meadow_daisy",
            name = "Meadow Daisy",
            botanicalName = "Bellis perennis",
            rarity = PlantRarity.COMMON,
            baseIncomePerSec = 2.5,
            baseSellValue = 85.0,
            growthDurationSec = 12,
            description = "Bright cheerful petals beloved by passing bees and local herbalists.",
            iconEmoji = "🌼",
            primaryColorHex = 0xFF81C784
        ),
        PlantSpecies(
            id = "lucky_clover",
            name = "Emerald Clover",
            botanicalName = "Trifolium repens",
            rarity = PlantRarity.COMMON,
            baseIncomePerSec = 4.0,
            baseSellValue = 140.0,
            growthDurationSec = 15,
            description = "Brings a steady drip of pocket fortune to any garden plot.",
            iconEmoji = "🍀",
            primaryColorHex = 0xFF2E7D32
        ),
        PlantSpecies(
            id = "sweet_basil",
            name = "Royal Basil",
            botanicalName = "Ocimum basilicum",
            rarity = PlantRarity.COMMON,
            baseIncomePerSec = 6.0,
            baseSellValue = 210.0,
            growthDurationSec = 18,
            description = "A staple herb prized by high-end gourmet restaurant chefs.",
            iconEmoji = "🌱",
            primaryColorHex = 0xFF388E3C
        ),

        // Uncommon
        PlantSpecies(
            id = "lavender_bell",
            name = "Lavender Bell",
            botanicalName = "Lavandula campana",
            rarity = PlantRarity.UNCOMMON,
            baseIncomePerSec = 16.0,
            baseSellValue = 600.0,
            growthDurationSec = 22,
            description = "Produces sweet aromatherapeutic nectar harvested for luxury perfumeries.",
            iconEmoji = "🪻",
            primaryColorHex = 0xFF7E57C2
        ),
        PlantSpecies(
            id = "copper_cactus",
            name = "Copper Spire Cactus",
            botanicalName = "Echinocactus cuprum",
            rarity = PlantRarity.UNCOMMON,
            baseIncomePerSec = 28.0,
            baseSellValue = 1100.0,
            growthDurationSec = 26,
            description = "Metallic spines absorb static energy and convert it into pure currency.",
            iconEmoji = "🌵",
            primaryColorHex = 0xFF0288D1
        ),
        PlantSpecies(
            id = "velvet_fern",
            name = "Velvet Shadow Fern",
            botanicalName = "Adiantum velutina",
            rarity = PlantRarity.UNCOMMON,
            baseIncomePerSec = 45.0,
            baseSellValue = 1800.0,
            growthDurationSec = 30,
            description = "Feathery fronds that glow with a gentle aquamarine luminescence.",
            iconEmoji = "🪴",
            primaryColorHex = 0xFF00897B
        ),

        // Rare
        PlantSpecies(
            id = "moonlight_lotus",
            name = "Moonlight Lotus",
            botanicalName = "Nelumbo nocturna",
            rarity = PlantRarity.RARE,
            baseIncomePerSec = 120.0,
            baseSellValue = 5200.0,
            growthDurationSec = 36,
            description = "Blooms only under silver skies, radiating moon pearls of immense value.",
            iconEmoji = "🪷",
            primaryColorHex = 0xFF8E24AA
        ),
        PlantSpecies(
            id = "frost_bloom",
            name = "Azure Frostbloom",
            botanicalName = "Cryoflora glacialis",
            rarity = PlantRarity.RARE,
            baseIncomePerSec = 210.0,
            baseSellValue = 9500.0,
            growthDurationSec = 42,
            description = "Sub-zero crystalline petals that sparkle like genuine sapphires.",
            iconEmoji = "❄️",
            primaryColorHex = 0xFF1E88E5
        ),
        PlantSpecies(
            id = "sunflare_marigold",
            name = "Sunflare Marigold",
            botanicalName = "Tagetes solaris",
            rarity = PlantRarity.RARE,
            baseIncomePerSec = 380.0,
            baseSellValue = 18000.0,
            growthDurationSec = 48,
            description = "Concentrates solar energy into molten droplets of pure gold.",
            iconEmoji = "🌻",
            primaryColorHex = 0xFFFB8C00
        ),

        // Epic
        PlantSpecies(
            id = "phoenix_rose",
            name = "Phoenix Ember Rose",
            botanicalName = "Rosa ignis rediviva",
            rarity = PlantRarity.EPIC,
            baseIncomePerSec = 1200.0,
            baseSellValue = 65000.0,
            growthDurationSec = 55,
            description = "Fiery petals that never wither. Collectors trade whole estates for a single bloom.",
            iconEmoji = "🌹",
            primaryColorHex = 0xFFD84315
        ),
        PlantSpecies(
            id = "starlight_dahlia",
            name = "Cosmic Starlight Dahlia",
            botanicalName = "Dahlia astraea",
            rarity = PlantRarity.EPIC,
            baseIncomePerSec = 2400.0,
            baseSellValue = 135000.0,
            growthDurationSec = 60,
            description = "Contains micro-stardust swirls within its deep indigo fractal blossoms.",
            iconEmoji = "✨",
            primaryColorHex = 0xFF6A1B9A
        ),
        PlantSpecies(
            id = "prismatic_bonsai",
            name = "Prismatic Bonsai",
            botanicalName = "Bonsai chromatica",
            rarity = PlantRarity.EPIC,
            baseIncomePerSec = 4800.0,
            baseSellValue = 280000.0,
            growthDurationSec = 70,
            description = "Miniature ancient tree reflecting every hue of the rainbow.",
            iconEmoji = "🌲",
            primaryColorHex = 0xFF00ACC1
        ),

        // Legendary
        PlantSpecies(
            id = "celestial_world_tree",
            name = "Yggdrasil Heart Sprout",
            botanicalName = "Arbor mundi aeterna",
            rarity = PlantRarity.LEGENDARY,
            baseIncomePerSec = 15000.0,
            baseSellValue = 1200000.0,
            growthDurationSec = 80,
            description = "A mythical sprout descended from the World Tree itself. Emits colossal cosmic wealth.",
            iconEmoji = "🌳",
            primaryColorHex = 0xFFFFB300
        ),
        PlantSpecies(
            id = "chrono_lily",
            name = "Chrono-Lily of Eternity",
            botanicalName = "Lilium temporis",
            rarity = PlantRarity.LEGENDARY,
            baseIncomePerSec = 35000.0,
            baseSellValue = 3000000.0,
            growthDurationSec = 90,
            description = "Bends the fabric of spacetime, yielding fortunes across dimensions.",
            iconEmoji = "🪐",
            primaryColorHex = 0xFFFF8F00
        ),
        PlantSpecies(
            id = "golden_ambrosia",
            name = "Golden Ambrosia Blossom",
            botanicalName = "Flora aurea divina",
            rarity = PlantRarity.LEGENDARY,
            baseIncomePerSec = 75000.0,
            baseSellValue = 7500000.0,
            growthDurationSec = 100,
            description = "The ultimate botanical specimen. Drips nectar of the gods and infinite riches.",
            iconEmoji = "👑",
            primaryColorHex = 0xFFFFD700
        )
    )

    fun getById(id: String): PlantSpecies {
        return allSpecies.find { it.id == id } ?: allSpecies.first()
    }
}
