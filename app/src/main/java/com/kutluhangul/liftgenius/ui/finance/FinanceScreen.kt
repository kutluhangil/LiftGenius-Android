@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.finance

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
import com.kutluhangul.liftgenius.ui.components.EmptyState
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.StatCard
import com.kutluhangul.liftgenius.ui.components.rememberPdfShareLauncher
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import kotlin.time.ExperimentalTime

@Composable
fun FinanceScreen(viewModel: FinanceViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val sharePdf = rememberPdfShareLauncher()

    when {
        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(
            message = uiState.error ?: stringResource(R.string.state_error_generic),
            onRetry = viewModel::load,
        )
        else -> FinanceContent(
            uiState = uiState,
            onExportPdf = {
                sharePdf { ctx ->
                    PdfExporter.exportFinance(ctx, uiState.packages, uiState.clientNames)
                }
            },
        )
    }
}

@Composable
private fun FinanceContent(
    uiState: FinanceViewModel.UiState,
    onExportPdf: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.tab_finance),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.weight(1f),
                )
                if (uiState.packages.isNotEmpty()) {
                    IconButton(onClick = onExportPdf) {
                        Icon(
                            Icons.Filled.PictureAsPdf,
                            contentDescription = stringResource(R.string.action_export_pdf),
                        )
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                StatCard(
                    value = Formatters.currency(uiState.paidTotal),
                    label = stringResource(R.string.finance_total_paid),
                    valueColor = MaterialTheme.extended.success,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = Formatters.currency(uiState.unpaidTotal),
                    label = stringResource(R.string.finance_total_unpaid),
                    valueColor = MaterialTheme.extended.warning,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(R.string.finance_packages),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (uiState.packages.isEmpty()) {
            item { EmptyState(stringResource(R.string.finance_empty)) }
        } else {
            items(uiState.packages, key = { it.id }) { pkg ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = uiState.clientNames[pkg.clientId] ?: "—",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Spacer(Modifier.height(Spacing.xs))
                            Text(
                                text = "${pkg.name} · ${Formatters.dayMonth(pkg.startDate)}",
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
    }
}
