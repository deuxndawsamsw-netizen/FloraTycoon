package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = Color(0xFF003914),
    primaryContainer = Color(0xFF135328),
    onPrimaryContainer = Color(0xFFA1F5AF),
    secondary = AmberGoldDark,
    onSecondary = Color(0xFF432C00),
    background = ForestDarkBackground,
    surface = ForestDarkSurface,
    surfaceVariant = ForestDarkSurfaceVariant,
    onBackground = Color(0xFFE1E8DF),
    onSurface = Color(0xFFE1E8DF),
    onSurfaceVariant = Color(0xFFBAC5B8)
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA1F5AF),
    onPrimaryContainer = Color(0xFF002108),
    secondary = AmberGold,
    onSecondary = Color(0xFF3E2723),
    background = ParchmentLightBackground,
    surface = ParchmentLightSurface,
    surfaceVariant = ParchmentLightSurfaceVariant,
    onBackground = Color(0xFF191D1A),
    onSurface = Color(0xFF191D1A),
    onSurfaceVariant = Color(0xFF424940)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
