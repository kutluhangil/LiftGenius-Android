package com.kutluhangul.liftgenius.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.IconField
import com.kutluhangul.liftgenius.ui.components.OrDivider
import com.kutluhangul.liftgenius.ui.components.ambientGlow
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .ambientGlow(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthHeader(
                title = stringResource(R.string.login_title),
                subtitle = stringResource(R.string.login_subtitle),
            )
            Spacer(Modifier.height(Spacing.xl))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                IconField(
                    icon = Icons.Filled.Email,
                    placeholder = stringResource(R.string.label_email),
                    value = email,
                    onValueChange = { email = it; viewModel.clearError() },
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.fillMaxWidth(),
                )
                HorizontalDivider(color = MaterialTheme.extended.ledgerDivider)
                IconField(
                    icon = Icons.Filled.Lock,
                    placeholder = stringResource(R.string.label_password),
                    value = password,
                    onValueChange = { password = it; viewModel.clearError() },
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                uiState.errorMessage?.let { message ->
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(Modifier.height(Spacing.lg))
                GradientButton(
                    text = stringResource(R.string.action_login),
                    onClick = { viewModel.login(email, password) },
                    loading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(Spacing.xl))
            OrDivider(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.xl))
            GoogleSignInButton(
                onToken = viewModel::loginWithGoogle,
                onError = viewModel::showError,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.md))
            TextButton(onClick = onRegisterClick) {
                Text(
                    text = stringResource(R.string.prompt_no_account),
                    color = MaterialTheme.extended.textSecondary,
                )
            }
        }
    }
}
