@file:OptIn(ExperimentalMaterial3Api::class)

package com.kutluhangul.liftgenius.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.DateField
import com.kutluhangul.liftgenius.ui.components.DropdownField
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.TimeField
import com.kutluhangul.liftgenius.ui.theme.Spacing
import java.time.LocalDate
import java.time.LocalTime

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
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

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
            Spacer(Modifier.height(Spacing.lg))
            DropdownField(
                label = stringResource(R.string.label_client),
                options = clientOptions,
                selectedId = clientId,
                onSelect = { clientId = it },
            )
            Spacer(Modifier.height(Spacing.lg))
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
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text(stringResource(R.string.label_duration)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.label_title)) },
                    singleLine = true,
                    modifier = Modifier.weight(2f),
                )
            }
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
                onClick = { onSave(clientId, date, time, duration, title, notes) },
                loading = isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}
