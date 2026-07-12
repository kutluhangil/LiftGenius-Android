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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.IconField
import com.kutluhangul.liftgenius.ui.components.OrDivider
import com.kutluhangul.liftgenius.ui.components.ambientGlow
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.OnAccent
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
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
        if (uiState.awaitingEmailConfirmation) {
            EmailConfirmationContent(onLoginClick = onLoginClick)
        } else {
            RegisterFormContent(
                uiState = uiState,
                onRegister = viewModel::register,
                onFieldChange = viewModel::clearError,
                onGoogleToken = viewModel::loginWithGoogle,
                onGoogleError = viewModel::showError,
                onLoginClick = onLoginClick,
            )
        }
    }
}

@Composable
private fun RegisterFormContent(
    uiState: AuthViewModel.UiState,
    onRegister: (fullName: String, email: String, password: String) -> Unit,
    onFieldChange: () -> Unit,
    onGoogleToken: (String) -> Unit,
    onGoogleError: (String) -> Unit,
    onLoginClick: () -> Unit,
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

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
            title = stringResource(R.string.register_title),
            subtitle = stringResource(R.string.register_subtitle),
        )
        Spacer(Modifier.height(Spacing.xl))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            IconField(
                icon = Icons.Filled.PersonAdd,
                placeholder = stringResource(R.string.label_full_name),
                value = fullName,
                onValueChange = { fullName = it; onFieldChange() },
                modifier = Modifier.fillMaxWidth(),
            )
            HorizontalDivider(color = MaterialTheme.extended.ledgerDivider)
            IconField(
                icon = Icons.Filled.Email,
                placeholder = stringResource(R.string.label_email),
                value = email,
                onValueChange = { email = it; onFieldChange() },
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth(),
            )
            HorizontalDivider(color = MaterialTheme.extended.ledgerDivider)
            IconField(
                icon = Icons.Filled.Lock,
                placeholder = stringResource(R.string.label_password),
                value = password,
                onValueChange = { password = it; onFieldChange() },
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
                text = stringResource(R.string.action_register),
                onClick = { onRegister(fullName, email, password) },
                loading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(Spacing.xl))
        OrDivider(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(Spacing.xl))
        GoogleSignInButton(
            onToken = onGoogleToken,
            onError = onGoogleError,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.md))
        TextButton(onClick = onLoginClick) {
            Text(
                text = stringResource(R.string.prompt_have_account),
                color = MaterialTheme.extended.textSecondary,
            )
        }
    }
}

@Composable
private fun EmailConfirmationContent(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .widthIn(max = 440.dp)
            .fillMaxWidth()
            .safeDrawingPadding()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.confirm_email_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = stringResource(R.string.confirm_email_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extended.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.xxxl))
        GradientButton(
            text = stringResource(R.string.action_back_to_login),
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Shared auth header: gradient circle icon + title + subtitle (iOS register/login). */
@Composable
fun AuthHeader(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(BrandGradient, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = null,
            tint = OnAccent,
        )
    }
    Spacer(Modifier.height(Spacing.lg))
    Text(text = title, style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(Spacing.xs))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.extended.textSecondary,
        textAlign = TextAlign.Center,
    )
}
