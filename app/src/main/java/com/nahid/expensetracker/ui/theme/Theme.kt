package com.nahid.expensetracker.ui.theme

import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA3DFBE),
    onPrimary = Color(0xFF102C20),
    primaryContainer = Color(0xFF203A30),
    onPrimaryContainer = Color(0xFFD3F2DE),
    secondary = Color(0xFFB7C9BA),
    tertiary = Color(0xFFE9C38E),
    tertiaryContainer = Color(0xFF3E3125),
    onTertiaryContainer = Color(0xFFF6D9AF),
    background = Color(0xFF141916),
    surface = Color(0xFF141916),
    onBackground = Color(0xFFE9EDE6),
    onSurface = Color(0xFFE9EDE6),
    onSurfaceVariant = Color(0xFFBEC8BE),
    surfaceContainerLow = Color(0xFF1C241F),
    surfaceContainer = Color(0xFF252E27),
    outlineVariant = Color(0xFF3A463D),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF18543D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1EFE4),
    onPrimaryContainer = Color(0xFF123A2A),
    secondary = Color(0xFF516559),
    tertiary = Color(0xFF805832),
    tertiaryContainer = Color(0xFFF2E7D8),
    onTertiaryContainer = Color(0xFF624223),
    background = Color(0xFFF7F5EF),
    surface = Color(0xFFF7F5EF),
    onBackground = Color(0xFF1A231E),
    onSurface = Color(0xFF1A231E),
    onSurfaceVariant = Color(0xFF505D54),
    surfaceContainerLow = Color(0xFFFFFEFA),
    surfaceContainer = Color(0xFFEDEDE5),
    outlineVariant = Color(0xFFDDDFD5),
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalIndication provides ripple()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
        ) {
            CompositionLocalProvider(LocalContentColor provides colorScheme.onBackground, content = content)
        }
    }
}