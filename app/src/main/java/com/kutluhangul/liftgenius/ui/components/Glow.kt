package com.kutluhangul.liftgenius.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kutluhangul.liftgenius.ui.theme.extended

/**
 * Ambient glow backdrop (DESIGN.md §5): orange top-left, pink bottom-right. Theme-aware —
 * colors come from the active [ExtendedColors] so light and dark both read correctly.
 * Apply after a background color, before content.
 */
@Composable
fun Modifier.ambientGlow(): Modifier {
    val topColor = MaterialTheme.extended.ambientTop
    val bottomColor = MaterialTheme.extended.ambientBottom
    return this.drawBehind {
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
}
