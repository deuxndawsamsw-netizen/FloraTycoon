package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeaderboardEntry
import com.example.util.Formatters

@Composable
fun LeaderboardScreen(
    leaderboardEntries: List<LeaderboardEntry>,
    lifetimeEarnings: Double,
    gardenIncomePerSec: Double,
    goldenPotTaps: Long,
    unlockedPlotsCount: Int,
    modifier: Modifier = Modifier
) {
    val playerEntry = leaderboardEntries.firstOrNull { it.isPlayer }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("leaderboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
    ) {
        // Player Tycoon Profile Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E2833)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFFFFD54F).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🧑‍🌾", fontSize = 32.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = playerEntry?.name ?: "You (Botanist Boss)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Global Rank: #${playerEntry?.rank ?: '-'}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFB300)
                        ) {
                            Text(
                                text = "TOP EARNER",
                                color = Color(0xFF3E2723),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Player Key Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Lifetime Wealth",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = Formatters.formatMoney(lifetimeEarnings),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784)
                            )
                        }

                        Column {
                            Text(
                                text = "Garden Rate",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = Formatters.formatIncomeRate(gardenIncomePerSec),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64B5F6)
                            )
                        }

                        Column {
                            Text(
                                text = "Golden Taps",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = Formatters.formatNumber(goldenPotTaps),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        Column {
                            Text(
                                text = "Plots",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "$unlockedPlotsCount/12",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(18.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GLOBAL HIGH EARNERS LEADERBOARD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Leaderboard entries
        items(leaderboardEntries) { entry ->
            val isTop3 = entry.rank <= 3
            val rankColor = when (entry.rank) {
                1 -> Color(0xFFFFD700) // Gold
                2 -> Color(0xFFC0C0C0) // Silver
                3 -> Color(0xFFCD7F32) // Bronze
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .then(
                        if (entry.isPlayer) {
                            Modifier.border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                        } else Modifier
                    )
                    .testTag("leaderboard_entry_${entry.rank}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (entry.isPlayer) Color(0xFF1B3828) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (entry.isPlayer || isTop3) 3.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Rank Number
                        Surface(
                            shape = CircleShape,
                            color = rankColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#${entry.rank}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = if (isTop3) rankColor else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Avatar
                        Text(text = entry.avatarEmoji, fontSize = 24.sp)

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = entry.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (entry.isPlayer) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                if (entry.isPlayer) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF4CAF50)
                                    ) {
                                        Text(
                                            text = "YOU",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 8.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (entry.isPlayer) Color(0xFF81C784) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Wealth Value
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Formatters.formatMoney(entry.netWorth),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isTop3) Color(0xFFFFB300) else Color(0xFF2E7D32)
                        )
                        Text(
                            text = Formatters.formatIncomeRate(entry.gardenIncomePerSec),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
