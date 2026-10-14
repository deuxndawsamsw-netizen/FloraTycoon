package com.example.model

import kotlin.random.Random

data class SeedPack(
    val id: String,
    val name: String,
    val cost: Double,
    val seedCount: Int,
    val description: String,
    val iconEmoji: String,
    val accentColorHex: Long,
    val rarityRates: Map<PlantRarity, Double>
)

object SeedPackCatalog {
    val packs: List<SeedPack> = listOf(
        SeedPack(
            id = "sprout_sachet",
            name = "Sprout Sachet",
            cost = 50.0,
            seedCount = 1,
            description = "Affordable starter packet. Perfect for humble fledgling gardens.",
            iconEmoji = "🌱",
            accentColorHex = 0xFF4CAF50,
            rarityRates = mapOf(
                PlantRarity.COMMON to 0.75,
                PlantRarity.UNCOMMON to 0.25
            )
        ),
        SeedPack(
            id = "botanist_parcel",
            name = "Botanist's Parcel",
            cost = 350.0,
            seedCount = 2,
            description = "Curated greenhouse selection with guaranteed quality and a chance of rare blooms.",
            iconEmoji = "📦",
            accentColorHex = 0xFF0288D1,
            rarityRates = mapOf(
                PlantRarity.COMMON to 0.40,
                PlantRarity.UNCOMMON to 0.45,
                PlantRarity.RARE to 0.15
            )
        ),
        SeedPack(
            id = "enchanted_cache",
            name = "Enchanted Flora Cache",
            cost = 2500.0,
            seedCount = 3,
            description = "High-tier mystical pouch glowing with amethyst dust. High chance of Rare & Epic species.",
            iconEmoji = "🔮",
            accentColorHex = 0xFF8E24AA,
            rarityRates = mapOf(
                PlantRarity.COMMON to 0.10,
                PlantRarity.UNCOMMON to 0.35,
                PlantRarity.RARE to 0.40,
                PlantRarity.EPIC to 0.15
            )
        ),
        SeedPack(
            id = "mythic_vault",
            name = "Mythic Sunburst Vault",
            cost = 25000.0,
            seedCount = 4,
            description = "Ancient vault of kings. Yields massive profit powerhouses including Legendary World Trees!",
            iconEmoji = "✨",
            accentColorHex = 0xFFFFB300,
            rarityRates = mapOf(
                PlantRarity.RARE to 0.25,
                PlantRarity.EPIC to 0.50,
                PlantRarity.LEGENDARY to 0.25
            )
        )
    )

    fun openPack(pack: SeedPack): List<PlantSpecies> {
        val results = mutableListOf<PlantSpecies>()
        repeat(pack.seedCount) {
            val roll = Random.nextDouble()
            var cumulative = 0.0
            var chosenRarity = PlantRarity.COMMON
            for ((rarity, rate) in pack.rarityRates) {
                cumulative += rate
                if (roll <= cumulative) {
                    chosenRarity = rarity
                    break
                }
            }

            val matchingSpecies = PlantCatalog.allSpecies.filter { it.rarity == chosenRarity }
            val chosenSpecies = if (matchingSpecies.isNotEmpty()) {
                matchingSpecies.random()
            } else {
                PlantCatalog.allSpecies.random()
            }
            results.add(chosenSpecies)
        }
        return results
    }
}
