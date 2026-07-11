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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.ambientGlow
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
        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = stringResource(R.string.register_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extended.textSecondary,
        )
        Spacer(Modifier.height(Spacing.xxxl))
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                onFieldChange()
            },
            label = { Text(stringResource(R.string.label_full_name)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.lg))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onFieldChange()
            },
            label = { Text(stringResource(R.string.label_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.lg))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                onFieldChange()
            },
            label = { Text(stringResource(R.string.label_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onRegister(fullName, email, password) }),
            modifier = Modifier.fillMaxWidth(),
        )
        uiState.errorMessage?.let { message ->
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.height(Spacing.xxl))
        GradientButton(
            text = stringResource(R.string.action_register),
            onClick = { onRegister(fullName, email, password) },
            loading = uiState.isLoading,
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
