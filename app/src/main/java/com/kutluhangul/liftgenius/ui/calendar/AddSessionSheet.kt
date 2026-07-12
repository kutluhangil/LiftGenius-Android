@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.kutluhangul.liftgenius.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.DateField
import com.kutluhangul.liftgenius.ui.components.DropdownField
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.SegmentedControl
import com.kutluhangul.liftgenius.ui.components.TimeField
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended
import java.time.LocalDate
import java.time.LocalTime

private val durations = listOf("30", "45", "60", "90")
private val focusOptions = listOf("Göğüs", "Sırt", "Bacak", "Omuz", "Kol")

@Composable
fun AddSessionSheet(
    clientOptions: List<Pair<String, String>>,
    initialDate: LocalDate,
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (
        clientId: String?,
        date: LocalDate,
        time: LocalTime,
        duration: String,
        title: String,
        notes: String,
    ) -> Unit,
) {
    var clientId by remember { mutableStateOf<String?>(null) }
    var date by remember { mutableStateOf(initialDate) }
    var time by remember { mutableStateOf(LocalTime.of(10, 0)) }
    var duration by remember { mutableStateOf("60") }
    var focus by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var intensity by remember { mutableStateOf("mid") }
    var notes by remember { mutableStateOf("") }

    val intensityOptions = listOf(
        "low" to stringResource(R.string.intensity_low),
        "mid" to stringResource(R.string.intensity_mid),
        "high" to stringResource(R.string.intensity_high),
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        ) {
            Text(
                text = stringResource(R.string.add_session_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            if (clientOptions.isEmpty()) {
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = stringResource(R.string.session_client_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            DropdownField(
                label = stringResource(R.string.label_client),
                options = clientOptions,
                selectedId = clientId,
                onSelect = { clientId = it },
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.label_date_time),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(Spacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                DateField(
                    label = stringResource(R.string.label_date),
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.weight(1f),
                )
                TimeField(
                    label = stringResource(R.string.label_time),
                    value = time,
                    onValueChange = { time = it },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.label_duration),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(Spacing.sm))
            SegmentedControl(
                options = durations,
                selected = duration,
                onSelect = { duration = it },
                optionLabel = { it },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.session_focus),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(Spacing.sm))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                focusOptions.forEach { option ->
                    FilterChip(
                        selected = focus == option,
                        onClick = { focus = if (focus == option) null else option },
                        label = { Text(option) },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.lg))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.label_title)) },
                placeholder = { Text(stringResource(R.string.session_title_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.session_intensity),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(Spacing.sm))
            SegmentedControl(
                options = intensityOptions,
                selected = intensityOptions.first { it.first == intensity },
                onSelect = { intensity = it.first },
                optionLabel = { it.second },
                highlightGradient = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.lg))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.label_notes)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            error?.let { message ->
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(Spacing.xl))
            GradientButton(
                text = stringResource(R.string.action_save),
                onClick = {
                    // Focus and intensity have no DB column; fold them into title/notes.
                    val finalTitle = title.trim().ifBlank { focus }.orEmpty()
                    val intensityLabel = intensityOptions.first { it.first == intensity }.second
                    val finalNotes = buildString {
                        append("Yoğunluk: $intensityLabel")
                        if (notes.isNotBlank()) append("\n${notes.trim()}")
                    }
                    onSave(clientId, date, time, duration, finalTitle, finalNotes)
                },
                loading = isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}
