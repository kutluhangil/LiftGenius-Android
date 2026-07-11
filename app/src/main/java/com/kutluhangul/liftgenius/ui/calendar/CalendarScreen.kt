package com.kutluhangul.liftgenius.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.components.EmptyState
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.SessionRow
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import java.time.LocalDate

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = Spacing.lg),
    ) {
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = stringResource(R.string.tab_calendar),
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(Spacing.md))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = { viewModel.shiftWeek(-1) }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = null)
            }
            Text(
                text = "${Formatters.dayMonth(uiState.weekStart)} – " +
                    Formatters.dayMonth(uiState.weekStart.plusDays(6)),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extended.textSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { viewModel.shiftWeek(1) }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = null)
            }
        }
        Spacer(Modifier.height(Spacing.sm))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.fillMaxWidth(),
        ) {
            uiState.weekDays.forEach { date ->
                DayChip(
                    date = date,
                    selected = date == uiState.selectedDate,
                    hasSessions = uiState.sessionsOn(date).isNotEmpty(),
                    onClick = { viewModel.selectDate(date) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(Spacing.md))
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(
                message = uiState.error ?: stringResource(R.string.state_error_generic),
                onRetry = viewModel::loadWeek,
            )
            else -> {
                val daySessions = uiState.sessionsOn(uiState.selectedDate)
                if (daySessions.isEmpty()) {
                    EmptyState(stringResource(R.string.calendar_empty))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = Spacing.xxl),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        items(daySessions, key = { it.id }) { session ->
                            SessionRow(
                                session = session,
                                clientName = uiState.clientNames[session.clientId] ?: "—",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    date: LocalDate,
    selected: Boolean,
    hasSessions: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm),
    ) {
        Text(
            text = Formatters.weekdayShort(date),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.extended.textSecondary,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
        )
        Spacer(Modifier.height(Spacing.xs))
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(
                    if (hasSessions) MaterialTheme.colorScheme.primary else background,
                ),
        )
    }
}
