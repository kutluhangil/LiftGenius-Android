package com.kutluhangul.liftgenius.ui.home

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.auth.SessionViewModel
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.ambientGlow
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

/** Placeholder main screen; the real dashboard lands with CLAUDE.md section 11, step 6. */
@Composable
fun HomeScreen(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val signOutError by sessionViewModel.signOutError.collectAsState()

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
            Text(
                text = stringResource(R.string.home_greeting_kicker),
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = sessionViewModel.currentUserEmail().orEmpty(),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.home_placeholder),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
            signOutError?.let { message ->
                Spacer(Modifier.height(Spacing.lg))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(Spacing.xxxl))
            GradientButton(
                text = stringResource(R.string.action_sign_out),
                onClick = sessionViewModel::signOut,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
