package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.model.GameUpgrade
import com.example.model.UpgradeCategory
import com.example.model.UpgradeCatalog
import com.example.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradesScreen(
    coins: Double,
    upgradeLevels: Map<String, Int>,
    onBuyUpgrade: (GameUpgrade) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = remember {
        listOf(
            Triple("Growth", Icons.Default.Speed, UpgradeCategory.GROWTH),
            Triple("Workers & Offline", Icons.Default.PrecisionManufacturing, UpgradeCategory.WORKERS_AUTOMATION),
            Triple("Golden Pot", Icons.Default.Bolt, UpgradeCategory.GOLDEN_POT)
        )
    }

    val currentCategory = categories[selectedCategoryIndex].third
    val filteredUpgrades = remember(currentCategory) {
        UpgradeCatalog.upgrades.filter { it.category == currentCategory }
    }

    // Status summary calculations
    val fertilizerLvl = upgradeLevels["mineral_fertilizer"] ?: 0
    val gnomeLvl = upgradeLevels["gnome_gardener"] ?: 0
    val clockworkLvl = upgradeLevels["clockwork_harvester"] ?: 0
    val foremanLvl = upgradeLevels["offline_foreman"] ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("upgrades_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
    ) {
        // Automation & Growth Overview Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2D21)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUTOMATION & SPEED STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (foremanLvl > 0) Color(0xFF2E7D32) else Color(0xFF757575)
                        ) {
                            Text(
                                text = if (foremanLvl > 0) "OFFLINE HARVEST ACTIVE" else "OFFLINE LOCKED",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Growth Speed",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "+${fertilizerLvl * 20}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF81C784)
                            )
                        }

                        Column {
                            Text(
                                text = "Automated Gnomes",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (gnomeLvl > 0) "Hired (Lv $gnomeLvl)" else "None",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        Column {
                            Text(
                                text = "Offline Capacity",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (foremanLvl > 0) "${foremanLvl * 2} Hours" else "0h",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF64B5F6)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Category Filter Tabs
        item {
            PrimaryTabRow(
                selectedTabIndex = selectedCategoryIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upgrade_category_tabs"),
                containerColor = Color.Transparent
            ) {
                categories.forEachIndexed { index, (title, icon, _) ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        icon = {
                            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Upgrades list
        items(filteredUpgrades) { upgrade ->
            val currentLvl = upgradeLevels[upgrade.id] ?: 0
            val cost = UpgradeCatalog.getCost(upgrade, currentLvl)
            val isMax = currentLvl >= upgrade.maxLevel
            val canAfford = coins >= cost && !isMax

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .testTag("upgrade_item_${upgrade.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = upgrade.iconEmoji, fontSize = 24.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = upgrade.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isMax) Color(0xFFFFB300).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isMax) "MAX" else "Lv $currentLvl/${upgrade.maxLevel}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMax) Color(0xFFFF8F00) else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Text(
                                    text = upgrade.effectDescription(currentLvl + 1),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = { onBuyUpgrade(upgrade) },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("buy_upgrade_${upgrade.id}_button")
                        ) {
                            Text(
                                text = if (isMax) "MAX" else Formatters.formatMoney(cost),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = upgrade.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
