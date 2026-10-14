package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfileFlow(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getPlayerProfile(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM garden_plots ORDER BY plotIndex ASC")
    fun getPlotsFlow(): Flow<List<PlotEntity>>

    @Query("SELECT * FROM garden_plots ORDER BY plotIndex ASC")
    suspend fun getPlots(): List<PlotEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlots(plots: List<PlotEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlot(plot: PlotEntity)

    @Query("SELECT * FROM seed_inventory WHERE count > 0")
    fun getSeedInventoryFlow(): Flow<List<SeedInventoryEntity>>

    @Query("SELECT * FROM seed_inventory WHERE count > 0")
    suspend fun getSeedInventory(): List<SeedInventoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSeedInventory(item: SeedInventoryEntity)

    @Query("DELETE FROM seed_inventory WHERE count <= 0")
    suspend fun cleanupEmptySeeds()

    @Query("SELECT * FROM game_upgrades")
    fun getUpgradesFlow(): Flow<List<UpgradeEntity>>

    @Query("SELECT * FROM game_upgrades")
    suspend fun getUpgrades(): List<UpgradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUpgrade(upgrade: UpgradeEntity)
}
