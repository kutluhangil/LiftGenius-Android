package com.kutluhangul.liftgenius.ui.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.pdf.PdfExporter
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.rememberPdfShareLauncher
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun WorkoutPlanScreen(
    onBack: () -> Unit,
    viewModel: WorkoutPlanViewModel = hiltViewModel(),
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
                text = uiState.plan?.title.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            uiState.plan?.let { plan ->
                IconButton(
                    onClick = {
                        val days = uiState.days.map { PdfExporter.DayContent(it.day, it.exercises) }
                        sharePdf { ctx ->
                            PdfExporter.exportWorkout(ctx, plan, days, uiState.clientName)
                        }
                    },
                ) {
                    Icon(
                        Icons.Filled.PictureAsPdf,
                        contentDescription = stringResource(R.string.action_export_pdf),
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
            else -> LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxSize(),
            ) {
                uiState.plan?.description?.let { description ->
                    item {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.extended.textSecondary,
                        )
                    }
                }
                items(uiState.days, key = { it.day.id }) { dayWithExercises ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = dayWithExercises.day.dayName,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(Modifier.height(Spacing.sm))
                        dayWithExercises.exercises.forEach { exercise ->
                            val weightSuffix = exercise.weight?.let { " ($it)" }.orEmpty()
                            Text(
                                text = "• ${exercise.name} — ${exercise.sets} × ${exercise.reps}$weightSuffix",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extended.textSecondary,
                            )
                            exercise.notes?.let { notes ->
                                Text(
                                    text = "   $notes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.extended.textTertiary,
                                )
                            }
                            Spacer(Modifier.height(Spacing.xs))
                        }
                    }
                }
            }
        }
    }
}
