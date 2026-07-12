package com.kutluhangul.liftgenius.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.SubPageScaffold
import com.kutluhangul.liftgenius.ui.components.sendSupportEmail
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun FeedbackScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val email = stringResource(R.string.support_email)
    var message by remember { mutableStateOf("") }

    SubPageScaffold(title = stringResource(R.string.feedback_title), onClose = onClose) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(Spacing.lg),
        ) {
            Icon(
                imageVector = Icons.Filled.ChatBubble,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(36.dp),
            )
            Spacer(Modifier.height(Spacing.md))
            Text(stringResource(R.string.feedback_title), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(R.string.feedback_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = stringResource(R.string.feedback_message_label),
                style = KickerLabel,
                color = MaterialTheme.extended.textTertiary,
            )
            Spacer(Modifier.height(Spacing.sm))
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text(stringResource(R.string.feedback_placeholder)) },
                minLines = 5,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.xl))
            GradientButton(
                text = stringResource(R.string.feedback_send),
                onClick = { sendSupportEmail(context, email, "LiftGenius — Geri Bildirim") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
