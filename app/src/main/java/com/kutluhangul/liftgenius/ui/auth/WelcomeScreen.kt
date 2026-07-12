package com.kutluhangul.liftgenius.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.NeonVideo
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

/** Onboarding entry (iOS "Başla" screen): looping neon video, wordmark, start / sign-in. */
@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onHaveAccount: () -> Unit,
) {
    val background = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
    ) {
        // Full-screen looping video background (center-cropped).
        NeonVideo(modifier = Modifier.fillMaxSize())

        // Top & bottom fade so the video melts into the background and text stays readable.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to background,
                        0.22f to background.copy(alpha = 0f),
                        0.55f to background.copy(alpha = 0f),
                        0.82f to background.copy(alpha = 0.85f),
                        1.0f to background,
                    ),
                ),
        )

        // Wordmark + actions overlaid on the video, anchored to the bottom.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = "LiftGenius",
                style = MaterialTheme.typography.displayLarge.merge(TextStyle(brush = BrandGradient)),
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = stringResource(R.string.welcome_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
            Spacer(Modifier.height(Spacing.xxxl))
            GradientButton(
                text = stringResource(R.string.action_start),
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.md))
            TextButton(onClick = onHaveAccount) {
                Text(
                    text = stringResource(R.string.prompt_have_account_short),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
