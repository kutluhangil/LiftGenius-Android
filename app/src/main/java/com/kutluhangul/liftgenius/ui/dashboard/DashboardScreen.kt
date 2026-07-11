package com.kutluhangul.liftgenius.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.components.EmptyState
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.SessionRow
import com.kutluhangul.liftgenius.ui.components.StatCard
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(
            message = uiState.error ?: stringResource(R.string.state_error_generic),
            onRetry = viewModel::load,
        )
        else -> DashboardContent(uiState)
    }
}

@Composable
private fun DashboardContent(uiState: DashboardViewModel.UiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            Text(
                text = stringResource(R.string.dashboard_kicker),
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.tab_dashboard),
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                StatCard(
                    value = uiState.activeClientCount.toString(),
                    label = stringResource(R.string.dashboard_active_clients),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = uiState.todaySessions.size.toString(),
                    label = stringResource(R.string.dashboard_today_sessions),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                StatCard(
                    value = Formatters.currency(uiState.monthRevenue),
                    label = stringResource(R.string.dashboard_month_revenue),
                    modifier = Modifier.weight(1f),
                    valueColor = MaterialTheme.extended.success,
                )
                StatCard(
                    value = Formatters.currency(uiState.unpaidTotal),
                    label = stringResource(R.string.dashboard_unpaid),
                    modifier = Modifier.weight(1f),
                    valueColor = MaterialTheme.extended.warning,
                )
            }
        }
        item {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(R.string.dashboard_today_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (uiState.todaySessions.isEmpty()) {
            item { EmptyState(stringResource(R.string.dashboard_no_sessions)) }
        } else {
            items(uiState.todaySessions, key = { it.id }) { session ->
                SessionRow(
                    session = session,
                    clientName = uiState.clientNames[session.clientId] ?: "—",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
