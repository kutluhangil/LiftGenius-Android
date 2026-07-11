package com.kutluhangul.liftgenius

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.ui.theme.Accent
import com.kutluhangul.liftgenius.ui.theme.AccentSecondary
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.LiftGeniusTheme
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dark-first theme: force dark system bars to match (see LiftGeniusTheme).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            LiftGeniusTheme {
                BrandScreen()
            }
        }
    }
}

/** Placeholder brand screen until the auth flow lands (CLAUDE.md section 11). */
@Composable
fun BrandScreen(modifier: Modifier = Modifier) {
    val glowOrange = Accent.copy(alpha = 0.18f)
    val glowPink = AccentSecondary.copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                // Ambient glow (DESIGN.md §5): orange top-left, pink bottom-right.
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(glowOrange, Color.Transparent),
                        center = Offset(size.width * 0.12f, size.height * 0.08f),
                        radius = size.maxDimension * 0.55f,
                    ),
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(glowPink, Color.Transparent),
                        center = Offset(size.width * 0.90f, size.height * 0.92f),
                        radius = size.maxDimension * 0.55f,
                    ),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PERSONAL TRAINER CRM",
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "LiftGenius",
                style = MaterialTheme.typography.displayLarge.merge(TextStyle(brush = BrandGradient)),
            )
            Spacer(Modifier.height(Spacing.lg))
            Box(
                Modifier
                    .width(56.dp)
                    .height(4.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(BrandGradient),
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = "Müşteriler · Programlar · Seanslar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0E13)
@Composable
fun BrandScreenPreview() {
    LiftGeniusTheme {
        BrandScreen()
    }
}
