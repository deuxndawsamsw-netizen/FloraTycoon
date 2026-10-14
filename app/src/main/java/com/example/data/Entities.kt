package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Double = 100.0,
    val lifetimeEarnings: Double = 100.0,
    val goldenPotTaps: Long = 0L,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val unlockedPlotsCount: Int = 4,
    val playerName: String = "Botanist Tycoon"
)

@Entity(tableName = "garden_plots")
data class PlotEntity(
    @PrimaryKey val plotIndex: Int,
    val speciesId: String? = null,
    val stage: String = "SPROUT",
    val growthProgress: Float = 0f,
    val plantedAt: Long = 0L,
    val evolutionLevel: Int = 1
)

@Entity(tableName = "seed_inventory")
data class SeedInventoryEntity(
    @PrimaryKey val speciesId: String,
    val count: Int = 1
)

@Entity(tableName = "game_upgrades")
data class UpgradeEntity(
    @PrimaryKey val upgradeId: String,
    val level: Int = 0
)
