@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.StatCard
import com.kutluhangul.liftgenius.ui.components.rememberPdfShareLauncher
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import kotlin.time.ExperimentalTime

@Composable
fun NutritionPlanScreen(
    onBack: () -> Unit,
    viewModel: NutritionPlanViewModel = hiltViewModel(),
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
                text = uiState.plan?.let { plan -> Formatters.fullDate(plan.createdAt) }.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            uiState.plan?.let { plan ->
                IconButton(
                    onClick = { sharePdf { ctx -> PdfExporter.exportNutrition(ctx, plan, uiState.clientName) } },
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
            uiState.plan != null -> {
                val plan = requireNotNull(uiState.plan)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatCard(
                            value = plan.dailyCalories.toString(),
                            label = stringResource(R.string.label_calories),
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            value = plan.proteinGrams.toString(),
                            label = stringResource(R.string.label_protein),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatCard(
                            value = plan.carbGrams.toString(),
                            label = stringResource(R.string.label_carbs),
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            value = plan.fatGrams.toString(),
                            label = stringResource(R.string.label_fat),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.meal_plan),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            text = plan.mealPlanText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.extended.textSecondary,
                        )
                        plan.notes?.let { notes ->
                            Spacer(Modifier.height(Spacing.sm))
                            Text(
                                text = notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extended.textTertiary,
                            )
                        }
                    }
                    Spacer(Modifier.height(Spacing.xxl))
                }
            }
        }
    }
}
