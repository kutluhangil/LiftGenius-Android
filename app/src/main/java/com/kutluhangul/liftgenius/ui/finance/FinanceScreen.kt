@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.finance

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.ClientPackage
import com.kutluhangul.liftgenius.pdf.PdfExporter
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.components.DonutChart
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.MonthlyLineChart
import com.kutluhangul.liftgenius.ui.components.ProgressRing
import com.kutluhangul.liftgenius.ui.components.rememberPdfShareLauncher
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import kotlin.time.ExperimentalTime

@Composable
fun FinanceScreen(
    onBack: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val sharePdf = rememberPdfShareLauncher()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                )
            }
            Text(
                text = stringResource(R.string.finance_analysis_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            if (uiState.packages.isNotEmpty()) {
                IconButton(
                    onClick = {
                        sharePdf { ctx ->
                            PdfExporter.exportFinance(ctx, uiState.packages, uiState.clientNames)
                        }
                    },
                ) {
                    Icon(
                        Icons.Filled.PictureAsPdf,
                        contentDescription = stringResource(R.string.action_export_pdf),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(
                message = uiState.error ?: stringResource(R.string.state_error_generic),
                onRetry = viewModel::load,
            )
            else -> FinanceContent(uiState)
        }
    }
}

@Composable
private fun FinanceContent(uiState: FinanceViewModel.UiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        item {
            Column {
                Text(
                    text = stringResource(R.string.finance_collected_total),
                    style = KickerLabel,
                    color = MaterialTheme.extended.textTertiary,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = Formatters.currency(uiState.paidTotal),
                    style = MaterialTheme.typography.displayLarge.merge(TextStyle(brush = BrandGradient)),
                )
                Text(
                    text = stringResource(
                        R.string.finance_paid_pending,
                        uiState.paidCount,
                        uiState.unpaidCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
        }
        item { DonutCard(uiState) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                MiniStat(
                    label = stringResource(R.string.finance_total_revenue),
                    value = Formatters.currency(uiState.totalRevenue),
                    modifier = Modifier.weight(1f),
                )
                MiniStat(
                    label = stringResource(R.string.finance_avg_package),
                    value = Formatters.currency(uiState.averagePackage),
                    modifier = Modifier.weight(1f),
                )
                MiniStat(
                    label = stringResource(R.string.finance_active_package),
                    value = uiState.activePackages.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Text(
                text = stringResource(R.string.finance_goal_streak),
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
        }
        item { GoalStreakCard(uiState) }
        item {
            Text(
                text = stringResource(R.string.finance_six_month),
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
        }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.finance_six_month_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
                Spacer(Modifier.height(Spacing.md))
                MonthlyLineChart(values = uiState.sixMonthValues, labels = uiState.sixMonthLabels)
            }
        }
        item {
            Text(
                text = stringResource(R.string.finance_pending_collections),
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
        }
        if (uiState.unpaidPackages.isEmpty()) {
            item { AllCollectedCard() }
        } else {
            items(uiState.unpaidPackages, key = { it.id }) { pkg ->
                PendingRow(pkg, uiState.clientNames[pkg.clientId] ?: "—")
            }
        }
    }
}

@Composable
private fun DonutCard(uiState: FinanceViewModel.UiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DonutChart(
                collectedFraction = uiState.collectedFraction,
                centerLabel = stringResource(R.string.finance_collection_rate),
                modifier = Modifier.weight(1.2f),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                LegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = stringResource(R.string.finance_collected),
                    value = Formatters.currency(uiState.paidTotal),
                )
                LegendItem(
                    color = MaterialTheme.extended.textTertiary,
                    label = stringResource(R.string.finance_pending),
                    value = Formatters.currency(uiState.unpaidTotal),
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(Spacing.sm))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.extended.textSecondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.extended.textTertiary,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun GoalStreakCard(uiState: FinanceViewModel.UiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(
                fraction = if (uiState.monthStreak > 0) 1f else 0f,
                centerValue = uiState.monthStreak.toString(),
                centerLabel = stringResource(R.string.finance_month_streak),
                modifier = Modifier.weight(1f),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                GoalRow(
                    label = stringResource(R.string.finance_income),
                    fraction = uiState.revenueFraction,
                    detail = "${Formatters.currency(uiState.paidTotal)} / ${Formatters.currency(uiState.revenueGoal)}",
                )
                GoalRow(
                    label = stringResource(R.string.finance_sessions),
                    fraction = uiState.sessionFraction,
                    detail = "${uiState.completedThisMonth} / ${uiState.sessionGoal}",
                )
            }
        }
    }
}

@Composable
private fun GoalRow(label: String, fraction: Float, detail: String) {
    Column {
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.extended.textSecondary,
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = "%${(fraction * 100).toInt()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(text = detail, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun AllCollectedCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = null,
                tint = MaterialTheme.extended.success,
            )
            Spacer(Modifier.width(Spacing.md))
            Text(
                text = stringResource(R.string.finance_all_collected),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
        }
    }
}

@Composable
private fun PendingRow(pkg: ClientPackage, clientName: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(clientName, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "${pkg.name} · ${Formatters.dayMonth(pkg.startDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
            Text(
                text = Formatters.currency(pkg.price),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extended.warning,
            )
        }
    }
}
