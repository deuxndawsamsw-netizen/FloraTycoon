package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PlantCatalog
import com.example.model.PlantRarity
import com.example.model.SeedPackCatalog
import com.example.model.UpgradeCatalog
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FloraTycoon", appName)
    }

    @Test
    fun `verify plant catalog species and rarities`() {
        val allSpecies = PlantCatalog.allSpecies
        assertTrue("Catalog should contain species", allSpecies.isNotEmpty())

        val legendarySpecies = allSpecies.filter { it.rarity == PlantRarity.LEGENDARY }
        assertTrue("Should have legendary species", legendarySpecies.isNotEmpty())

        val mint = PlantCatalog.getById("mint_sprout")
        assertNotNull(mint)
        assertEquals(PlantRarity.COMMON, mint.rarity)
    }

    @Test
    fun `verify seed pack catalog RNG rolls`() {
        val sproutPack = SeedPackCatalog.packs.first { it.id == "sprout_sachet" }
        val rolled = SeedPackCatalog.openPack(sproutPack)
        assertEquals(sproutPack.seedCount, rolled.size)
    }

    @Test
    fun `verify tycoon formatters`() {
        assertEquals("$0.00", Formatters.formatMoney(0.0))
        assertEquals("$150.0", Formatters.formatMoney(150.0))
        assertEquals("$2.50K", Formatters.formatMoney(2500.0))
        assertEquals("$1.50M", Formatters.formatMoney(1_500_000.0))
        assertEquals("$5.00B", Formatters.formatMoney(5_000_000_000.0))
    }

    @Test
    fun `verify upgrade catalog costs`() {
        val fertilizer = UpgradeCatalog.upgrades.first { it.id == "mineral_fertilizer" }
        val costLv0 = UpgradeCatalog.getCost(fertilizer, 0)
        val costLv1 = UpgradeCatalog.getCost(fertilizer, 1)
        assertTrue("Level 1 cost should exceed level 0", costLv1 > costLv0)
    }
}
