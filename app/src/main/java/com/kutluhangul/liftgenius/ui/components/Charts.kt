package com.kutluhangul.liftgenius.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.ui.theme.Accent
import com.kutluhangul.liftgenius.ui.theme.AccentSecondary
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

/** Weekly session-intensity bars (Dashboard). Zero values render as a faint baseline. */
@Composable
fun WeeklyBarChart(
    values: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    val gridColor = MaterialTheme.extended.ledgerDivider
    val maxValue = (values.maxOrNull() ?: 0).coerceAtLeast(1)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            val barCount = values.size.coerceAtLeast(1)
            val slot = size.width / barCount
            val barWidth = slot * 0.42f
            values.forEachIndexed { index, value ->
                val fraction = value.toFloat() / maxValue
                val barHeight = (size.height * fraction).coerceAtLeast(2f)
                val left = index * slot + (slot - barWidth) / 2f
                val top = size.height - barHeight
                if (value == 0) {
                    drawLine(
                        color = gridColor,
                        start = Offset(left, size.height - 2f),
                        end = Offset(left + barWidth, size.height - 2f),
                        strokeWidth = 3f,
                    )
                } else {
                    drawRoundRect(
                        brush = BrandGradient,
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                    )
                }
            }
        }
        Spacer(Modifier.height(Spacing.sm))
        ChartLabels(labels)
    }
}

/** Monthly revenue trend as a gradient line with dots (Dashboard / Finance). */
@Composable
fun MonthlyLineChart(
    values: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    val gridColor = MaterialTheme.extended.ledgerDivider
    val maxValue = (values.maxOrNull() ?: 0.0).coerceAtLeast(1.0)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            if (values.isEmpty()) return@Canvas
            val stepX = if (values.size > 1) size.width / (values.size - 1) else 0f
            // baseline dashes
            val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 8f))
            for (row in 0..3) {
                val y = size.height * row / 3f
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                    pathEffect = dash,
                )
            }
            val points = values.mapIndexed { index, value ->
                val fraction = (value / maxValue).toFloat()
                Offset(index * stepX, size.height - (size.height * fraction).coerceAtLeast(2f))
            }
            for (i in 0 until points.size - 1) {
                drawLine(
                    brush = Brush.horizontalGradient(listOf(Accent, AccentSecondary)),
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 5f,
                )
            }
            points.forEachIndexed { index, point ->
                val dotColor = if (index < points.size / 2) Accent else AccentSecondary
                drawCircle(color = dotColor, radius = 7f, center = point)
            }
        }
        Spacer(Modifier.height(Spacing.sm))
        ChartLabels(labels)
    }
}

/** Collection-rate donut: orange arc = collected, muted arc = pending. Center shows percent. */
@Composable
fun DonutChart(
    collectedFraction: Float,
    centerLabel: String,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.extended.ledgerDivider
    val percent = (collectedFraction.coerceIn(0f, 1f) * 100).toInt()

    androidx.compose.foundation.layout.Box(
        modifier = modifier.height(140.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            val stroke = 26f
            val diameter = size.height - stroke
            val topLeft = Offset((size.width - diameter) / 2f, stroke / 2f)
            val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
            if (collectedFraction > 0f) {
                drawArc(
                    brush = BrandGradient,
                    startAngle = -90f,
                    sweepAngle = 360f * collectedFraction.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%$percent",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.extended.textTertiary,
            )
        }
    }
}

/** Circular progress ring with a center value/label (streak or goal). */
@Composable
fun ProgressRing(
    fraction: Float,
    centerValue: String,
    centerLabel: String,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.extended.ledgerDivider

    androidx.compose.foundation.layout.Box(
        modifier = modifier.height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val stroke = 14f
            val diameter = size.height - stroke
            val topLeft = Offset((size.width - diameter) / 2f, stroke / 2f)
            val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
            if (fraction > 0f) {
                drawArc(
                    brush = BrandGradient,
                    startAngle = -90f,
                    sweepAngle = 360f * fraction.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerValue,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.extended.textTertiary,
            )
        }
    }
}

@Composable
private fun ChartLabels(labels: List<String>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        labels.forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.extended.textTertiary,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
