@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.Session
import com.kutluhangul.liftgenius.domain.model.SessionStatus
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.SessionStatusPill
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import kotlin.time.ExperimentalTime

/** Bottom sheet to change a session's status or delete it. */
@Composable
fun SessionActionsSheet(
    session: Session,
    clientName: String,
    isMutating: Boolean,
    error: String?,
    onSelectStatus: (SessionStatus) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        ) {
            Text(clientName, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "${Formatters.dayMonth(session.date)} · ${Formatters.time(session.date)} · " +
                    "${session.durationMinutes} dk",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extended.textSecondary,
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.session_status_title),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(Spacing.sm))
            SessionStatus.entries.forEach { status ->
                val isCurrent = session.status == status
                OutlinedButton(
                    onClick = { onSelectStatus(status) },
                    enabled = !isMutating,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SessionStatusPill(status)
                    Text(
                        text = status.label(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = Spacing.md),
                    )
                }
                Spacer(Modifier.height(Spacing.sm))
            }
            error?.let { message ->
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(Spacing.md))
            TextButton(
                onClick = onDelete,
                enabled = !isMutating,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = stringResource(R.string.action_delete),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = Spacing.sm),
                )
            }
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}
