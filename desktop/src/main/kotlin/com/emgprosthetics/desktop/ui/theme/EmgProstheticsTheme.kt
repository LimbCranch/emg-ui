// desktop/src/main/kotlin/com/emgprosthetics/desktop/ui/theme/EmgProstheticsTheme.kt
package com.emgprosthetics.desktop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Color palette for EMG application
object EmgColors {
    // Primary colors - Medical blue theme
    val Primary = Color(0xFF00D4FF)
    val PrimaryVariant = Color(0xFF0099CC)
    val Secondary = Color(0xFF00FF88)

    // Background colors
    val Background = Color(0xFF1E1E2E)
    val Surface = Color(0xFF2A2A3E)
    val SurfaceVariant = Color(0xFF404040)

    // Signal colors for visualization
    val SignalChannel1 = Color(0xFF00D4FF)
    val SignalChannel2 = Color(0xFF FF6B00)
    val SignalChannel3 = Color(0xFF00FF88)
    val SignalChannel4 = Color(0xFFFF3366)

    // Status colors
    val Success = Color(0xFF00FF88)
    val Warning = Color(0xFFFFB800)
    val Error = Color(0xFFFF3366)
    val Info = Color(0xFF00D4FF)

    // Text colors
    val OnBackground = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFFE0E0E0)
    val OnSurfaceVariant = Color(0xFFB0B0B0)
}

// Typography
object EmgTypography {
    val headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )

    val headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )

    val titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    val bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    val bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

    val labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = EmgColors.Primary,
    secondary = EmgColors.Secondary,
    background = EmgColors.Background,
    surface = EmgColors.Surface,
    surfaceVariant = EmgColors.SurfaceVariant,
    onBackground = EmgColors.OnBackground,
    onSurface = EmgColors.OnSurface,
    onSurfaceVariant = EmgColors.OnSurfaceVariant,
    error = EmgColors.Error
)

// Light color scheme (for future use)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),
    secondary = Color(0xFF00AA66),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun EmgProstheticsTheme(
    darkTheme: Boolean = true, // Always use dark theme for medical equipment
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            headlineLarge = EmgTypography.headlineLarge,
            headlineMedium = EmgTypography.headlineMedium,
            titleLarge = EmgTypography.titleLarge,
            bodyLarge = EmgTypography.bodyLarge,
            bodyMedium = EmgTypography.bodyMedium,
            labelMedium = EmgTypography.labelMedium
        ),
        content = content
    )
}