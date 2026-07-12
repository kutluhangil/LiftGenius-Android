package com.kutluhangul.liftgenius.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.BrandWordmark
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.OrDivider
import com.kutluhangul.liftgenius.ui.components.ambientGlow
import com.kutluhangul.liftgenius.ui.theme.LiftGeniusTheme
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
                .padding(Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BrandWordmark()
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.welcome_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
            Spacer(Modifier.height(Spacing.xxxl))
            GradientButton(
                text = stringResource(R.string.action_login),
                onClick = onLoginClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.md))
            OutlinedButton(
                onClick = onRegisterClick,
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.action_register),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
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
            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(Spacing.lg))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0E13)
@Composable
fun WelcomeScreenPreview() {
    LiftGeniusTheme {
        WelcomeScreen(onLoginClick = {}, onRegisterClick = {})
    }
}
