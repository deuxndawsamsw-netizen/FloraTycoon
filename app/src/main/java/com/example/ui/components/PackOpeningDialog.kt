package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PlantSpecies
import com.example.util.Formatters
import com.example.viewmodel.PackOpeningState

@Composable
fun PackOpeningDialog(
    packState: PackOpeningState,
    onRevealNext: () -> Unit,
    onCollectAll: () -> Unit
) {
    val pack = packState.pack
    val allRevealed = packState.revealedIndex >= packState.seeds.size - 1

    Dialog(onDismissRequest = { if (allRevealed) onCollectAll() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1B2A22)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("pack_opening_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = pack.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pack.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Text(
                    text = if (allRevealed) "Seeds Added to Pouch!" else "Tap cards or button to reveal seeds",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Revealed Cards list
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    itemsIndexed(packState.seeds) { index, species ->
                        val isRevealed = index <= packState.revealedIndex

                        SeedCardItem(
                            species = species,
                            isRevealed = isRevealed,
                            onClick = {
                                if (!isRevealed && index == packState.revealedIndex + 1) {
                                    onRevealNext()
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (!allRevealed) {
                    Button(
                        onClick = onRevealNext,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reveal_next_button")
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD54F))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reveal Next Seed", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onCollectAll,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("collect_all_seeds_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF3E2723))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Collect All Seeds to Pouch",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF3E2723)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeedCardItem(
    species: PlantSpecies,
    isRevealed: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .width(130.dp)
            .height(185.dp)
            .scale(scale)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isRevealed) Color(species.rarity.colorHex) else Color(0xFF4A6553),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRevealed) Color(0xFF23352B) else Color(0xFF131F17)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        if (!isRevealed) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "❓", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "MYSTERY SEED",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tap to Flip",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF81C784),
                        fontSize = 10.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(species.rarity.colorHex).copy(alpha = 0.3f)
                ) {
                    Text(
                        text = species.rarity.displayName.uppercase(),
                        color = Color(species.rarity.colorHex),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = CircleShape,
                    color = Color(species.primaryColorHex).copy(alpha = 0.25f),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = species.iconEmoji, fontSize = 28.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = species.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "+${Formatters.formatMoney(species.baseIncomePerSec)}/s",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF81C784),
                    fontSize = 10.sp
                )

                Text(
                    text = "Sell: $${Formatters.formatMoney(species.baseSellValue)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 9.sp
                )
            }
        }
    }
}
