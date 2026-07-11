package com.kutluhangul.liftgenius.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kutluhangul.liftgenius.ui.theme.Accent
import com.kutluhangul.liftgenius.ui.theme.AccentSecondary

/**
 * Ambient glow backdrop (DESIGN.md §5): orange top-left, pink bottom-right.
 * Apply after a background color, before content.
 */
fun Modifier.ambientGlow(
    topColor: Color = Accent.copy(alpha = 0.18f),
    bottomColor: Color = AccentSecondary.copy(alpha = 0.12f),
): Modifier = drawBehind {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(topColor, Color.Transparent),
            center = Offset(size.width * 0.12f, size.height * 0.08f),
            radius = size.maxDimension * 0.55f,
        ),
    )
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(bottomColor, Color.Transparent),
            center = Offset(size.width * 0.90f, size.height * 0.92f),
            radius = size.maxDimension * 0.55f,
        ),
    )
}
