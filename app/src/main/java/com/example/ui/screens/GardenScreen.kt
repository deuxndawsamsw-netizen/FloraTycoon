package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GrowthStage
import com.example.model.PlantRarity
import com.example.util.Formatters
import com.example.viewmodel.PlotUiState

@Composable
fun GardenScreen(
    plots: List<PlotUiState>,
    unlockedPlotsCount: Int,
    nextPlotCost: Double,
    playerCoins: Double,
    onPlotClick: (PlotUiState) -> Unit,
    onEmptyPlotClick: (plotIndex: Int) -> Unit,
    onUnlockNextPlot: () -> Unit,
    onNurturePlot: (plotIndex: Int) -> Unit,
    onSellPlot: (plotIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .testTag("garden_plots_grid"),
        contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Grid item plots
        items(plots) { plot ->
            PlotCard(
                plot = plot,
                onCardClick = {
                    if (plot.isUnlocked) {
                        if (plot.species != null) {
                            onPlotClick(plot)
                        } else {
                            onEmptyPlotClick(plot.plotIndex)
                        }
                    } else {
                        onUnlockNextPlot()
                    }
                },
                onNurture = { onNurturePlot(plot.plotIndex) },
                onSell = { onSellPlot(plot.plotIndex) }
            )
        }

        // Expand Greenhouse Plot Banner
        if (unlockedPlotsCount < 12) {
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("expand_greenhouse_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Expand Greenhouse",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Unlock Plot #${unlockedPlotsCount + 1} ($unlockedPlotsCount/12 active)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onUnlockNextPlot,
                            enabled = playerCoins >= nextPlotCost,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier.testTag("unlock_next_plot_button")
                        ) {
                            Text(
                                text = Formatters.formatMoney(nextPlotCost),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlotCard(
    plot: PlotUiState,
    onCardClick: () -> Unit,
    onNurture: () -> Unit,
    onSell: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable(onClick = onCardClick)
            .testTag("plot_card_${plot.plotIndex}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (plot.isUnlocked) {
                if (plot.species != null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (plot.species != null) 3.dp else 1.dp)
    ) {
        if (!plot.isUnlocked) {
            // Locked plot view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Plot #${plot.plotIndex + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap to Expand",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                }
            }
        } else if (plot.species == null) {
            // Empty plot view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Plant Seed",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Plot #${plot.plotIndex + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Plant Seed",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            // Active Plant View
            val species = plot.species
            val isMature = plot.stage == GrowthStage.MATURE || plot.stage == GrowthStage.EVOLVED

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: Plot number and Rarity badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${plot.plotIndex + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(species.rarity.colorHex).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = species.rarity.displayName.uppercase(),
                            color = Color(species.rarity.colorHex),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Plant Glyph & Name
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = Color(species.primaryColorHex).copy(alpha = 0.18f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = species.iconEmoji, fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = species.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    Text(
                        text = "+${Formatters.formatMoney(plot.currentIncomePerSec)}/s",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Growth progress bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isMature) "MATURE 🌟" else "${(plot.growthProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = if (isMature) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        if (!isMature) {
                            Text(
                                text = "${plot.remainingGrowthSec}s",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    LinearProgressIndicator(
                        progress = { plot.growthProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = if (isMature) Color(0xFF2E7D32) else Color(0xFF4CAF50),
                        trackColor = Color(0xFFE0E0E0)
                    )
                }

                // Actions: Grow it or Sell it buttons!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Grow / Evolve Button
                    Button(
                        onClick = onNurture,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("plot_${plot.plotIndex}_grow_button"),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMature) Color(0xFF1565C0) else Color(0xFF2E7D32)
                        )
                    ) {
                        Text(
                            text = if (isMature) "Evolve" else "Grow 💧",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Sell Button
                    Button(
                        onClick = onSell,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("plot_${plot.plotIndex}_sell_button"),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                    ) {
                        Text(
                            text = "Sell $",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
