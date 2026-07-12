package com.kutluhangul.liftgenius.ui.team

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.TrainerProfile
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.components.EmptyState
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.InitialsAvatar
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun TeamScreen(
    onBack: () -> Unit,
    viewModel: TeamViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
                text = stringResource(R.string.team_title),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(
                message = uiState.error ?: stringResource(R.string.state_error_generic),
                onRetry = viewModel::load,
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxSize(),
            ) {
                item { SalonSummaryCard(uiState) }
                item {
                    Text(
                        text = stringResource(R.string.team_trainers),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = Spacing.sm),
                    )
                }
                if (uiState.members.isEmpty()) {
                    item { EmptyState(stringResource(R.string.team_empty)) }
                } else {
                    items(uiState.members, key = { it.id }) { member ->
                        TeamMemberRow(
                            member = member,
                            isCurrentUser = member.id == uiState.currentUserId,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SalonSummaryCard(uiState: TeamViewModel.UiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.team_salon_overview),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.extended.textSecondary,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = uiState.salonName ?: stringResource(R.string.team_default_salon),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Icon(
                imageVector = Icons.Filled.Business,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(Modifier.height(Spacing.md))
        HorizontalDivider(color = MaterialTheme.extended.ledgerDivider)
        Spacer(Modifier.height(Spacing.md))
        Row(modifier = Modifier.fillMaxWidth()) {
            SummaryStat(uiState.clientCount.toString(), stringResource(R.string.team_clients), Modifier.weight(1f))
            SummaryStat(uiState.monthSessions.toString(), stringResource(R.string.team_sessions), Modifier.weight(1f))
            SummaryStat(
                Formatters.currency(uiState.monthRevenue),
                stringResource(R.string.team_revenue),
                Modifier.weight(1f),
                valueColor = MaterialTheme.extended.success,
            )
        }
    }
}

@Composable
private fun SummaryStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = valueColor)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.extended.textTertiary,
        )
    }
}

@Composable
private fun TeamMemberRow(member: TrainerProfile, isCurrentUser: Boolean) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InitialsAvatar(name = member.fullName)
            Spacer(Modifier.width(Spacing.md))
            Column(Modifier.weight(1f)) {
                Text(member.fullName, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = member.salonName?.let { "${member.role.label()} · $it" }
                        ?: member.role.label(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
            if (isCurrentUser) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                ) {
                    Text(
                        text = stringResource(R.string.team_you),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = Spacing.md,
                            vertical = Spacing.xs,
                        ),
                    )
                }
            }
        }
    }
}
