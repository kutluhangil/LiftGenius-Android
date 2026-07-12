package com.kutluhangul.liftgenius.ui.auth

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kutluhangul.liftgenius.BuildConfig
import com.kutluhangul.liftgenius.R
import kotlinx.coroutines.launch

/**
 * "Sign in with Google" via Credential Manager (CLAUDE.md section 5, step 2).
 * Retrieves a Google ID token whose audience is the Web client ID, then hands it up so the
 * caller can exchange it with Supabase. The Web client ID lives in BuildConfig (local.properties).
 */
@Composable
fun GoogleSignInButton(
    onToken: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
    val notConfigured = stringResource(R.string.google_not_configured)
    val failed = stringResource(R.string.google_failed)

    OutlinedButton(
        onClick = {
            if (webClientId.isBlank()) {
                onError(notConfigured)
                return@OutlinedButton
            }
            scope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)
                    val option = GetSignInWithGoogleOption.Builder(webClientId).build()
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(option)
                        .build()
                    val response = credentialManager.getCredential(context, request)
                    val credential = response.credential
                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        onToken(googleCredential.idToken)
                    } else {
                        onError(failed)
                    }
                } catch (e: GetCredentialCancellationException) {
                    // User dismissed the Google sheet — no error to surface.
                } catch (e: GetCredentialException) {
                    onError(e.message ?: failed)
                }
            }
        },
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.height(52.dp),
    ) {
        Text(
            text = stringResource(R.string.google_continue),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
