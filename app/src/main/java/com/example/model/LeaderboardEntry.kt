package com.example.model

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val title: String,
    val netWorth: Double,
    val gardenIncomePerSec: Double,
    val avatarEmoji: String,
    val isPlayer: Boolean = false,
    val badge: String = ""
)

object LeaderboardPresets {
    val rivals: List<LeaderboardEntry> = listOf(
        LeaderboardEntry(
            rank = 1,
            name = "Aurelius the Alchemist",
            title = "Supreme Flora Emperor",
            netWorth = 250_000_000.0,
            gardenIncomePerSec = 450_000.0,
            avatarEmoji = "👑",
            badge = "Legend"
        ),
        LeaderboardEntry(
            rank = 2,
            name = "Lady Seraphina Bloom",
            title = "Celestial Botanist",
            netWorth = 85_000_000.0,
            gardenIncomePerSec = 180_000.0,
            avatarEmoji = "🪷",
            badge = "Master"
        ),
        LeaderboardEntry(
            rank = 3,
            name = "Baron Von Chlorophyll",
            title = "Industrial Greenhouse Tycoon",
            netWorth = 32_000_000.0,
            gardenIncomePerSec = 75_000.0,
            avatarEmoji = "🎩",
            badge = "Master"
        ),
        LeaderboardEntry(
            rank = 4,
            name = "Grandmaster Sprout",
            title = "Ancient Bonsai Sage",
            netWorth = 12_500_000.0,
            gardenIncomePerSec = 30_000.0,
            avatarEmoji = "🧙‍♂️",
            badge = "Expert"
        ),
        LeaderboardEntry(
            rank = 5,
            name = "Cyber Florist Neo",
            title = "Hydroponic Architect",
            netWorth = 4_800_000.0,
            gardenIncomePerSec = 12_000.0,
            avatarEmoji = "🤖",
            badge = "Expert"
        ),
        LeaderboardEntry(
            rank = 6,
            name = "Farmer Giles",
            title = "Fertilizer Pioneer",
            netWorth = 1_500_000.0,
            gardenIncomePerSec = 4_500.0,
            avatarEmoji = "🧑‍🌾",
            badge = "Pro"
        ),
        LeaderboardEntry(
            rank = 7,
            name = "Duchess Petunia",
            title = "Orchid Connoisseur",
            netWorth = 450_000.0,
            gardenIncomePerSec = 1_200.0,
            avatarEmoji = "🌸",
            badge = "Pro"
        ),
        LeaderboardEntry(
            rank = 8,
            name = "Barnaby Badger",
            title = "Master Seed Merchant",
            netWorth = 120_000.0,
            gardenIncomePerSec = 400.0,
            avatarEmoji = "🦡",
            badge = "Merchant"
        ),
        LeaderboardEntry(
            rank = 9,
            name = "Daisy Miller",
            title = "Backyard Gardener",
            netWorth = 25_000.0,
            gardenIncomePerSec = 80.0,
            avatarEmoji = "🌻",
            badge = "Novice"
        ),
        LeaderboardEntry(
            rank = 10,
            name = "Sproutling Sam",
            title = "Apprentice Planter",
            netWorth = 3_000.0,
            gardenIncomePerSec = 15.0,
            avatarEmoji = "🌱",
            badge = "Novice"
        )
    )
}
