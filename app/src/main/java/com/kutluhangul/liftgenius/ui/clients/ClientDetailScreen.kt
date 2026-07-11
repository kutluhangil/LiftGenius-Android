@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.components.ClientStatusChip
import com.kutluhangul.liftgenius.ui.components.EmptyState
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.InitialsAvatar
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import kotlin.time.ExperimentalTime

private enum class DetailSheet { NONE, PACKAGE, PROGRESS, PR }

@Composable
fun ClientDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    onOpenAiWorkout: () -> Unit,
    onOpenAiNutrition: () -> Unit,
    onOpenWorkoutPlan: (String) -> Unit,
    onOpenNutritionPlan: (String) -> Unit,
    refreshRequested: Boolean = false,
    onRefreshConsumed: () -> Unit = {},
    viewModel: ClientDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var activeSheet by remember { mutableStateOf(DetailSheet.NONE) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(refreshRequested) {
        if (refreshRequested) {
            viewModel.load()
            onRefreshConsumed()
        }
    }
    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) onDeleted()
    }
    LaunchedEffect(uiState.mutationCompleted) {
        if (uiState.mutationCompleted) {
            activeSheet = DetailSheet.NONE
            viewModel.consumeMutation()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                )
            }
            Text(
                text = uiState.client?.fullName.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            uiState.client?.let { client ->
                IconButton(onClick = { onEdit(client.id) }) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.action_edit),
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = MaterialTheme.colorScheme.error,
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
            uiState.client != null -> DetailContent(
                uiState = uiState,
                onAddPackage = {
                    viewModel.consumeMutation()
                    activeSheet = DetailSheet.PACKAGE
                },
                onAddProgress = {
                    viewModel.consumeMutation()
                    activeSheet = DetailSheet.PROGRESS
                },
                onAddPr = {
                    viewModel.consumeMutation()
                    activeSheet = DetailSheet.PR
                },
                onOpenAiWorkout = onOpenAiWorkout,
                onOpenAiNutrition = onOpenAiNutrition,
                onOpenWorkoutPlan = onOpenWorkoutPlan,
                onOpenNutritionPlan = onOpenNutritionPlan,
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_client_title)) },
            text = { Text(stringResource(R.string.delete_client_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteClient()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    when (activeSheet) {
        DetailSheet.PACKAGE -> AddPackageSheet(
            isSaving = uiState.isMutating,
            error = uiState.mutationError,
            onDismiss = { activeSheet = DetailSheet.NONE },
            onSave = viewModel::addPackage,
        )
        DetailSheet.PROGRESS -> AddProgressSheet(
            isSaving = uiState.isMutating,
            error = uiState.mutationError,
            onDismiss = { activeSheet = DetailSheet.NONE },
            onSave = viewModel::addProgress,
        )
        DetailSheet.PR -> AddPrSheet(
            isSaving = uiState.isMutating,
            error = uiState.mutationError,
            onDismiss = { activeSheet = DetailSheet.NONE },
            onSave = viewModel::addPr,
        )
        DetailSheet.NONE -> Unit
    }
}

@Composable
private fun DetailContent(
    uiState: ClientDetailViewModel.UiState,
    onAddPackage: () -> Unit,
    onAddProgress: () -> Unit,
    onAddPr: () -> Unit,
    onOpenAiWorkout: () -> Unit,
    onOpenAiNutrition: () -> Unit,
    onOpenWorkoutPlan: (String) -> Unit,
    onOpenNutritionPlan: (String) -> Unit,
) {
    val client = requireNotNull(uiState.client)

    LazyColumn(
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier.fillMaxSize(),
    ) {
        item { HeaderSection(client) }
        item { ContactSection(client) }

        item {
            SectionHeader(
                title = stringResource(R.string.client_detail_packages),
                onAdd = onAddPackage,
            )
        }
        if (uiState.packages.isEmpty()) {
            item { EmptyState(stringResource(R.string.detail_empty_packages)) }
        } else {
            items(uiState.packages, key = { it.id }) { pkg ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(pkg.name, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = stringResource(
                                    R.string.sessions_remaining,
                                    pkg.remainingSessions,
                                    pkg.totalSessions,
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extended.textSecondary,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = Formatters.currency(pkg.price),
                                style = MaterialTheme.typography.titleSmall,
                                color = if (pkg.isPaid) {
                                    MaterialTheme.extended.success
                                } else {
                                    MaterialTheme.extended.warning
                                },
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = stringResource(
                                    if (pkg.isPaid) R.string.finance_paid else R.string.finance_unpaid,
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.extended.textSecondary,
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.client_detail_workout_plans),
                onAdd = onOpenAiWorkout,
                actionLabel = stringResource(R.string.action_generate_ai),
            )
        }
        if (uiState.workoutPlans.isEmpty()) {
            item { EmptyState(stringResource(R.string.detail_empty_workout)) }
        } else {
            items(uiState.workoutPlans, key = { it.id }) { plan ->
                GlassCard(
                    onClick = { onOpenWorkoutPlan(plan.id) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(plan.title, style = MaterialTheme.typography.titleSmall)
                            plan.description?.let { description ->
                                Spacer(Modifier.height(Spacing.xs))
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.extended.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Text(
                            text = Formatters.dayMonth(plan.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.extended.textSecondary,
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.client_detail_nutrition_plans),
                onAdd = onOpenAiNutrition,
                actionLabel = stringResource(R.string.action_generate_ai),
            )
        }
        if (uiState.nutritionPlans.isEmpty()) {
            item { EmptyState(stringResource(R.string.detail_empty_nutrition)) }
        } else {
            items(uiState.nutritionPlans, key = { it.id }) { plan ->
                GlassCard(
                    onClick = { onOpenNutritionPlan(plan.id) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "${plan.dailyCalories} kcal",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = "P ${plan.proteinGrams}g · K ${plan.carbGrams}g · Y ${plan.fatGrams}g",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extended.textSecondary,
                            )
                        }
                        Text(
                            text = Formatters.dayMonth(plan.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.extended.textSecondary,
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.client_detail_progress),
                onAdd = onAddProgress,
            )
        }
        val latestProgress = uiState.progress.firstOrNull()
        if (latestProgress == null) {
            item { EmptyState(stringResource(R.string.detail_empty_progress)) }
        } else {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = Formatters.fullDate(latestProgress.date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.extended.textSecondary,
                    )
                    Spacer(Modifier.height(Spacing.sm))
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xxl)) {
                        latestProgress.weight?.let { MeasurementItem("Kilo", "$it kg") }
                        latestProgress.bodyFat?.let { MeasurementItem("Yağ", "%$it") }
                        latestProgress.muscleMass?.let { MeasurementItem("Kas", "$it kg") }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.client_detail_prs),
                onAdd = onAddPr,
            )
        }
        if (uiState.prs.isEmpty()) {
            item { EmptyState(stringResource(R.string.detail_empty_prs)) }
        } else {
            items(uiState.prs, key = { it.id }) { pr ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(pr.exerciseName, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = Formatters.dayMonth(pr.date),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extended.textSecondary,
                            )
                        }
                        Text(
                            text = "${pr.weight} kg × ${pr.reps}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onAdd: () -> Unit, actionLabel: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onAdd) {
            Text(actionLabel ?: "+ ${stringResource(R.string.action_add)}")
        }
    }
}

@Composable
private fun HeaderSection(client: Client) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        InitialsAvatar(name = client.fullName, size = 64.dp)
        Spacer(Modifier.width(Spacing.lg))
        Column(Modifier.weight(1f)) {
            Text(client.fullName, style = MaterialTheme.typography.headlineSmall)
            client.goal?.let { goal ->
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = goal.label(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
        }
        ClientStatusChip(client.status)
    }
}

@Composable
private fun ContactSection(client: Client) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.client_detail_contact),
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(Spacing.sm))
        InfoRow(stringResource(R.string.label_phone), client.phone)
        InfoRow(stringResource(R.string.label_email), client.email)
        InfoRow(stringResource(R.string.label_weight), client.weight?.let { "$it kg" })
        InfoRow(stringResource(R.string.label_height), client.height?.let { "$it cm" })
        InfoRow(stringResource(R.string.label_notes), client.notes)
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extended.textSecondary,
            modifier = Modifier.width(96.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(Spacing.xs))
}

@Composable
private fun MeasurementItem(label: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.extended.textSecondary,
        )
    }
}
