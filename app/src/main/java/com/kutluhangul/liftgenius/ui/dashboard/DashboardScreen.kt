@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.MonthlyLineChart
import com.kutluhangul.liftgenius.ui.components.SessionRow
import com.kutluhangul.liftgenius.ui.components.WeeklyBarChart
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.ExperimentalTime

@Composable
fun DashboardScreen(
    onAddClient: () -> Unit,
    onAddSession: () -> Unit,
    onOpenFinance: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(
            message = uiState.error ?: stringResource(R.string.state_error_generic),
            onRetry = viewModel::load,
        )
        else -> DashboardContent(uiState, onAddClient, onAddSession, onOpenFinance)
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardViewModel.UiState,
    onAddClient: () -> Unit,
    onAddSession: () -> Unit,
    onOpenFinance: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        item { GreetingHeader(uiState.trainerName) }
        item { RevenueHero(uiState.monthRevenue) }
        item { StatsCard(uiState) }
        item {
            Text(
                text = stringResource(R.string.dashboard_performance),
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
        }
        item { WeeklyIntensityCard(uiState) }
        item { MonthlyTrendCard(uiState) }
        item {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.dashboard_today_title),
                    style = KickerLabel,
                    color = MaterialTheme.extended.textTertiary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.dashboard_session_count, uiState.todaySessions.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
        }
        if (uiState.todaySessions.isEmpty()) {
            item { RestDayCard() }
        } else {
            items(uiState.todaySessions, key = { it.id }) { session ->
                SessionRow(
                    session = session,
                    clientName = uiState.clientNames[session.clientId] ?: "—",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                QuickAction(
                    icon = Icons.Filled.PersonAddAlt1,
                    label = stringResource(R.string.dashboard_action_new_client),
                    onClick = onAddClient,
                    modifier = Modifier.weight(1f),
                )
                QuickAction(
                    icon = Icons.Filled.CalendarMonth,
                    label = stringResource(R.string.dashboard_action_add_session),
                    onClick = onAddSession,
                    modifier = Modifier.weight(1f),
                )
                QuickAction(
                    icon = Icons.Filled.Payments,
                    label = stringResource(R.string.tab_finance),
                    onClick = onOpenFinance,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun GreetingHeader(trainerName: String) {
    val zone = java.time.ZoneId.systemDefault()
    val dateText = LocalDate.now(zone)
        .format(DateTimeFormatter.ofPattern("d MMMM — EEEE", Locale.forLanguageTag("tr-TR")))
        .uppercase(Locale.forLanguageTag("tr-TR"))
    val greeting = when (LocalTime.now(zone).hour) {
        in 5..11 -> stringResource(R.string.greeting_morning)
        in 12..17 -> stringResource(R.string.greeting_afternoon)
        else -> stringResource(R.string.greeting_evening)
    }
    Column {
        Text(
            text = dateText,
            style = KickerLabel,
            color = MaterialTheme.extended.textTertiary,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = if (trainerName.isBlank()) greeting else "$greeting, $trainerName",
            style = MaterialTheme.typography.headlineLarge,
        )
    }
}

@Composable
private fun RevenueHero(amount: Double) {
    Column {
        Text(
            text = stringResource(R.string.dashboard_month_revenue_kicker),
            style = KickerLabel,
            color = MaterialTheme.extended.textTertiary,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = Formatters.currency(amount),
            style = MaterialTheme.typography.displayLarge.merge(TextStyle(brush = BrandGradient)),
        )
    }
}

@Composable
private fun StatsCard(uiState: DashboardViewModel.UiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        StatRow(stringResource(R.string.dashboard_active_clients), uiState.activeClientCount.toString())
        StatDivider()
        StatRow(stringResource(R.string.dashboard_today_sessions), uiState.todaySessions.size.toString())
        StatDivider()
        StatRow(stringResource(R.string.dashboard_finished_packages), uiState.finishedPackages.toString())
        StatDivider()
        StatRow(stringResource(R.string.dashboard_overdue), uiState.unpaidCount.toString())
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(Locale.forLanguageTag("tr-TR")),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.extended.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun StatDivider() {
    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.extended.ledgerDivider)
}

@Composable
private fun WeeklyIntensityCard(uiState: DashboardViewModel.UiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.dashboard_weekly_intensity),
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = if (uiState.weeklyBars.all { it == 0 }) {
                stringResource(R.string.dashboard_weekly_empty)
            } else {
                stringResource(R.string.dashboard_weekly_subtitle)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extended.textSecondary,
        )
        Spacer(Modifier.height(Spacing.md))
        WeeklyBarChart(values = uiState.weeklyBars, labels = uiState.weeklyLabels)
    }
}

@Composable
private fun MonthlyTrendCard(uiState: DashboardViewModel.UiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.dashboard_monthly_trend),
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = stringResource(R.string.dashboard_monthly_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extended.textSecondary,
        )
        Spacer(Modifier.height(Spacing.md))
        MonthlyLineChart(values = uiState.monthlyValues, labels = uiState.monthlyLabels)
    }
}

@Composable
private fun RestDayCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Bedtime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "  ${stringResource(R.string.dashboard_rest_day)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}
