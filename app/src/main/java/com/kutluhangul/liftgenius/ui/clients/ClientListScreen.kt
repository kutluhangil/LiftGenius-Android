package com.kutluhangul.liftgenius.ui.clients

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.components.ClientStatusChip
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.InitialsAvatar
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.theme.OnAccent
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun ClientListScreen(
    onClientClick: (String) -> Unit,
    onAddClick: () -> Unit,
    refreshRequested: Boolean = false,
    onRefreshConsumed: () -> Unit = {},
    viewModel: ClientListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(refreshRequested) {
        if (refreshRequested) {
            viewModel.load()
            onRefreshConsumed()
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.tab_clients),
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(Modifier.height(Spacing.lg))
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text(stringResource(R.string.clients_search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.md))
            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorState(
                    message = uiState.error ?: stringResource(R.string.state_error_generic),
                    onRetry = viewModel::load,
                )
                uiState.filteredClients.isEmpty() -> ClientsEmptyState(onAddClick)
                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    items(uiState.filteredClients, key = { it.id }) { client ->
                        ClientRow(client = client, onClick = { onClientClick(client.id) })
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = OnAccent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.xxl),
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.clients_add_title))
        }
    }
}

@Composable
private fun ClientsEmptyState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.GroupOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.height(Spacing.xl))
        Text(
            text = stringResource(R.string.clients_empty),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = stringResource(R.string.clients_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extended.textSecondary,
        )
        Spacer(Modifier.height(Spacing.xl))
        GradientButton(
            text = stringResource(R.string.clients_add_button),
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ClientRow(client: Client, onClick: () -> Unit) {
    GlassCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InitialsAvatar(name = client.fullName)
            Spacer(Modifier.width(Spacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = client.fullName,
                    style = MaterialTheme.typography.titleSmall,
                )
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
}
