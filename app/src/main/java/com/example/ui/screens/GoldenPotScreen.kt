package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.GameUpgrade
import com.example.model.UpgradeCategory
import com.example.model.UpgradeCatalog
import com.example.util.Formatters
import com.example.viewmodel.FloatingParticle
import kotlinx.coroutines.launch

@Composable
fun GoldenPotScreen(
    coins: Double,
    gardenIncomePerSec: Double,
    goldenPotTaps: Long,
    goldenFeverActive: Boolean,
    goldenFeverRemainingSec: Int,
    goldenFeverCharge: Float,
    upgradeLevels: Map<String, Int>,
    floatingParticles: List<FloatingParticle>,
    onTapGoldenPot: () -> Unit,
    onBuyUpgrade: (GameUpgrade) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

    val canLvl = upgradeLevels["golden_watering_can"] ?: 0
    val synergyLvl = upgradeLevels["golden_dew_synergy"] ?: 0
    val critLvl = upgradeLevels["sunburst_crit"] ?: 0

    val baseClick = 1.0 + (canLvl * 2.0) + (canLvl * canLvl * 0.5)
    val synergyClick = gardenIncomePerSec * (synergyLvl * 0.03)
    val currentTapPower = (baseClick + synergyClick) * (if (goldenFeverActive) 3.0 else 1.0)

    val goldenUpgrades = remember {
        UpgradeCatalog.upgrades.filter { it.category == UpgradeCategory.GOLDEN_POT }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("golden_pot_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Header & Tap Stats
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2C2411)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TAP POWER",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "+${Formatters.formatMoney(currentTapPower)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0xFFFFD54F).copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL TAPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = Formatters.formatNumber(goldenPotTaps),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Golden Fever Gauge
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (goldenFeverActive) "🔥 FEVER ACTIVE: 3X TAP SURGE (${goldenFeverRemainingSec}s)" else "Golden Fever Gauge",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (goldenFeverActive) Color(0xFFFF6F00) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (goldenFeverActive) "ACTIVE" else "${(goldenFeverCharge * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (goldenFeverActive) (goldenFeverRemainingSec / 20f) else goldenFeverCharge },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFB300),
                        trackColor = Color(0xFF423B2A)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // THE GOLDEN POT SPECIAL PLANT TAP TARGET
        item {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                if (goldenFeverActive) Color(0xFFFFD54F).copy(alpha = 0.45f) else Color(0xFFFFD54F).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            coroutineScope.launch {
                                scaleAnim.snapTo(0.88f)
                                scaleAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                            onTapGoldenPot()
                        }
                    )
                    .testTag("golden_pot_tap_target"),
                contentAlignment = Alignment.Center
            ) {
                // Golden Pot Artwork
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    modifier = Modifier
                        .size(210.dp)
                        .scale(scaleAnim.value)
                        .shadow(16.dp, CircleShape)
                        .border(
                            width = if (goldenFeverActive) 4.dp else 2.dp,
                            color = if (goldenFeverActive) Color(0xFFFFB300) else Color(0xFFFFD54F).copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.golden_plant_pot)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Special Golden Pot Plant",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Floating numbers
                floatingParticles.takeLast(4).forEach { p ->
                    Text(
                        text = p.text,
                        color = Color(p.colorHex),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = (p.id % 60 + 20).toInt().dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "TAP THE GOLDEN PLANT FOR INSTANT GOLD!",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFB300),
                    letterSpacing = 0.5.sp
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Golden Pot Upgrades Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Golden Pot Alchemy Upgrades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(goldenUpgrades.size) { index ->
            val upgrade = goldenUpgrades[index]
            val currentLvl = upgradeLevels[upgrade.id] ?: 0
            val cost = UpgradeCatalog.getCost(upgrade, currentLvl)
            val isMax = currentLvl >= upgrade.maxLevel
            val canAfford = coins >= cost && !isMax

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("golden_upgrade_${upgrade.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = upgrade.iconEmoji, fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = upgrade.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Lv $currentLvl/${upgrade.maxLevel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = upgrade.effectDescription(currentLvl + 1),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onBuyUpgrade(upgrade) },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("buy_upgrade_${upgrade.id}")
                    ) {
                        Text(
                            text = if (isMax) "MAX" else Formatters.formatMoney(cost),
                            color = if (canAfford) Color(0xFF3E2723) else Color.Gray,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
