package com.kutluhangul.liftgenius.ui.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.data.remote.GeneratedWorkout
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiWorkoutScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AiWorkoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                    )
                }
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = stringResource(R.string.ai_workout_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Spacer(Modifier.height(Spacing.xl))

            val result = uiState.result
            if (result == null) {
                WorkoutForm(
                    isGenerating = uiState.isGenerating,
                    error = uiState.generateError ?: uiState.clientError,
                    onGenerate = viewModel::generate,
                )
            } else {
                WorkoutPreview(
                    result = result,
                    isSaving = uiState.isSaving,
                    error = uiState.generateError,
                    onSave = viewModel::save,
                    onRegenerate = viewModel::regenerate,
                )
            }
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkoutForm(
    isGenerating: Boolean,
    error: String?,
    onGenerate: (days: Int, level: String, equipment: String, preferences: String) -> Unit,
) {
    var days by remember { mutableIntStateOf(3) }
    val levels = listOf(
        stringResource(R.string.level_beginner),
        stringResource(R.string.level_intermediate),
        stringResource(R.string.level_advanced),
    )
    var level by remember { mutableStateOf(levels[1]) }
    var equipment by rememberSaveable { mutableStateOf("") }
    var preferences by rememberSaveable { mutableStateOf("") }

    Text(
        text = stringResource(R.string.ai_days_per_week),
        style = MaterialTheme.typography.titleSmall,
    )
    Spacer(Modifier.height(Spacing.sm))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        (2..6).forEach { option ->
            FilterChip(
                selected = days == option,
                onClick = { days = option },
                label = { Text(option.toString()) },
            )
        }
    }
    Spacer(Modifier.height(Spacing.xl))
    Text(
        text = stringResource(R.string.ai_level),
        style = MaterialTheme.typography.titleSmall,
    )
    Spacer(Modifier.height(Spacing.sm))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        levels.forEach { option ->
            FilterChip(
                selected = level == option,
                onClick = { level = option },
                label = { Text(option) },
            )
        }
    }
    Spacer(Modifier.height(Spacing.xl))
    OutlinedTextField(
        value = equipment,
        onValueChange = { equipment = it },
        label = { Text(stringResource(R.string.ai_equipment)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(Spacing.lg))
    OutlinedTextField(
        value = preferences,
        onValueChange = { preferences = it },
        label = { Text(stringResource(R.string.ai_preferences)) },
        minLines = 2,
        modifier = Modifier.fillMaxWidth(),
    )
    error?.let { message ->
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
    Spacer(Modifier.height(Spacing.xxl))
    GradientButton(
        text = stringResource(R.string.ai_generate),
        onClick = { onGenerate(days, level, equipment, preferences) },
        loading = isGenerating,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun WorkoutPreview(
    result: GeneratedWorkout,
    isSaving: Boolean,
    error: String?,
    onSave: () -> Unit,
    onRegenerate: () -> Unit,
) {
    Text(result.title, style = MaterialTheme.typography.titleLarge)
    result.description?.let { description ->
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extended.textSecondary,
        )
    }
    Spacer(Modifier.height(Spacing.lg))
    result.days.forEach { day ->
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(day.dayName, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(Spacing.sm))
            day.exercises.forEach { exercise ->
                val weightSuffix = exercise.weight?.let { " ($it)" }.orEmpty()
                Text(
                    text = "• ${exercise.name} — ${exercise.sets} × ${exercise.reps}$weightSuffix",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
                Spacer(Modifier.height(Spacing.xs))
            }
        }
        Spacer(Modifier.height(Spacing.md))
    }
    error?.let { message ->
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
    Spacer(Modifier.height(Spacing.lg))
    GradientButton(
        text = stringResource(R.string.action_save),
        onClick = onSave,
        loading = isSaving,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(Spacing.sm))
    TextButton(
        onClick = onRegenerate,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.ai_regenerate))
    }
}
