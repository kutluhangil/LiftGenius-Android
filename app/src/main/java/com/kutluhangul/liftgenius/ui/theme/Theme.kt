package com.kutluhangul.liftgenius.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Brand tokens that have no Material 3 color-scheme slot (DESIGN.md §2, §4, §5).
 * Access via `MaterialTheme.extended`.
 */
@Immutable
data class ExtendedColors(
    val success: Color,
    val warning: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val cardBorder: Color,
    val glassHairline: Color,
    val glassSpecular: Color,
    val ledgerDivider: Color,
    val brandGradient: Brush,
    val ambientTop: Color,
    val ambientBottom: Color,
)

private val DarkExtendedColors = ExtendedColors(
    success = Success,
    warning = Warning,
    textSecondary = TextSecondaryDark,
    textTertiary = TextTertiaryDark,
    cardBorder = CardBorderDark,
    glassHairline = GlassHairlineDark,
    glassSpecular = GlassSpecularDark,
    ledgerDivider = LedgerDividerDark,
    brandGradient = BrandGradient,
    ambientTop = Accent.copy(alpha = 0.18f),
    ambientBottom = AccentSecondary.copy(alpha = 0.12f),
)

private val LightExtendedColors = ExtendedColors(
    success = Success,
    warning = Warning,
    textSecondary = TextSecondaryLight,
    textTertiary = TextTertiaryLight,
    cardBorder = CardBorderLight,
    glassHairline = GlassHairlineLight,
    glassSpecular = GlassSpecularLight,
    ledgerDivider = LedgerDividerLight,
    brandGradient = BrandGradient,
    ambientTop = Accent.copy(alpha = 0.12f),
    ambientBottom = AccentSecondary.copy(alpha = 0.10f),
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    primaryContainer = AccentContainerDark,
    onPrimaryContainer = OnAccentContainerDark,
    secondary = AccentSecondary,
    onSecondary = OnAccentSecondary,
    secondaryContainer = AccentSecondaryContainerDark,
    onSecondaryContainer = OnAccentSecondaryContainerDark,
    tertiary = AccentMid,
    onTertiary = OnAccent,
    background = BgPrimaryDark,
    onBackground = TextPrimaryDark,
    surface = BgPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = BgTertiaryDark,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerLowest = Color(0xFF0B0A0F),
    surfaceContainerLow = BgSecondaryDark,
    surfaceContainer = BgSecondaryDark,
    surfaceContainerHigh = BgTertiaryDark,
    surfaceContainerHighest = Color(0xFF2C2B35),
    error = Danger,
    onError = Color(0xFF2B0508),
    errorContainer = Color(0xFF3D1215),
    onErrorContainer = Color(0xFFFFC9CD),
    outline = GlassHairlineDark,
    outlineVariant = LedgerDividerDark,
)

private val LightColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    primaryContainer = AccentContainerLight,
    onPrimaryContainer = OnAccentContainerLight,
    secondary = AccentSecondary,
    onSecondary = OnAccentSecondary,
    secondaryContainer = AccentSecondaryContainerLight,
    onSecondaryContainer = OnAccentSecondaryContainerLight,
    tertiary = AccentMid,
    onTertiary = OnAccent,
    background = BgPrimaryLight,
    onBackground = TextPrimaryLight,
    surface = BgSecondaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BgTertiaryLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = BgSecondaryLight,
    surfaceContainer = Color(0xFFF3F0F6),
    surfaceContainerHigh = BgTertiaryLight,
    surfaceContainerHighest = Color(0xFFE2DEE8),
    error = Danger,
    onError = Color.White,
    errorContainer = Color(0xFFFFD9DB),
    onErrorContainer = Color(0xFF40060C),
    outline = TextPrimaryLight.copy(alpha = 0.12f),
    outlineVariant = LedgerDividerLight,
)

@Composable
fun LiftGeniusTheme(
    // Dark-first "Obsidian Glass" brand (DESIGN.md §5). The value is driven by the user's
    // Karanlık Mod preference (ThemePreferences); defaults to dark.
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
    ) {
        // Default content color for text/icons drawn outside a Surface — without this the
        // Material default is black, which vanishes on the dark background.
        CompositionLocalProvider(
            LocalExtendedColors provides extendedColors,
            LocalContentColor provides colorScheme.onBackground,
            content = content,
        )
    }
}

val MaterialTheme.extended: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
