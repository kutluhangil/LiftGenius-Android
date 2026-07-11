@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kutluhangul.liftgenius.domain.model.Session
import com.kutluhangul.liftgenius.domain.model.SessionStatus
import com.kutluhangul.liftgenius.ui.common.Formatters
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import kotlin.time.ExperimentalTime

/** One session in a list: client, title, time and status. */
@Composable
fun SessionRow(
    session: Session,
    clientName: String,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = clientName,
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = session.title ?: "Antrenman · ${session.durationMinutes} dk",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatters.time(session.date),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(Spacing.xs))
                SessionStatusPill(session.status)
            }
        }
    }
}

@Composable
fun SessionStatusPill(status: SessionStatus, modifier: Modifier = Modifier) {
    val tint = when (status) {
        SessionStatus.SCHEDULED -> MaterialTheme.colorScheme.primary
        SessionStatus.COMPLETED -> MaterialTheme.extended.success
        SessionStatus.CANCELLED -> MaterialTheme.extended.textTertiary
        SessionStatus.NO_SHOW -> MaterialTheme.colorScheme.error
    }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = tint.copy(alpha = 0.14f),
    ) {
        Text(
            text = status.label(),
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        )
    }
}
