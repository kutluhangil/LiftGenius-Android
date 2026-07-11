@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.kutluhangul.liftgenius.ui.clients

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.PaymentMethod
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.components.DateField
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.theme.Spacing
import java.time.LocalDate

@Composable
private fun SheetScaffold(
    title: String,
    error: String?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
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
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(Spacing.lg))
            content()
            error?.let { message ->
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
fun AddPackageSheet(
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        totalSessions: String,
        price: String,
        method: PaymentMethod?,
        isPaid: Boolean,
        startDate: LocalDate,
    ) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var totalSessions by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var method by remember { mutableStateOf<PaymentMethod?>(null) }
    var isPaid by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(LocalDate.now()) }

    SheetScaffold(
        title = stringResource(R.string.add_package_title),
        error = error,
        onDismiss = onDismiss,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.label_package_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.lg))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            OutlinedTextField(
                value = totalSessions,
                onValueChange = { totalSessions = it },
                label = { Text(stringResource(R.string.label_total_sessions)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(stringResource(R.string.label_price)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(Spacing.lg))
        DateField(
            label = stringResource(R.string.label_date),
            value = startDate,
            onValueChange = { startDate = it },
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = stringResource(R.string.label_payment_method),
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(Spacing.sm))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            PaymentMethod.entries.forEach { option ->
                FilterChip(
                    selected = method == option,
                    onClick = { method = if (method == option) null else option },
                    label = { Text(option.label()) },
                )
            }
        }
        Spacer(Modifier.height(Spacing.lg))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.label_is_paid),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = isPaid, onCheckedChange = { isPaid = it })
        }
        Spacer(Modifier.height(Spacing.xl))
        GradientButton(
            text = stringResource(R.string.action_save),
            onClick = { onSave(name, totalSessions, price, method, isPaid, startDate) },
            loading = isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun AddProgressSheet(
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (
        date: LocalDate,
        weight: String,
        bodyFat: String,
        muscleMass: String,
        chest: String,
        armLeft: String,
        armRight: String,
        waist: String,
        hips: String,
    ) -> Unit,
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var muscleMass by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var armLeft by remember { mutableStateOf("") }
    var armRight by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hips by remember { mutableStateOf("") }

    @Composable
    fun measurementField(value: String, onChange: (String) -> Unit, labelRes: Int, modifier: Modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text(stringResource(labelRes)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = modifier,
        )
    }

    SheetScaffold(
        title = stringResource(R.string.add_progress_title),
        error = error,
        onDismiss = onDismiss,
    ) {
        DateField(
            label = stringResource(R.string.label_date),
            value = date,
            onValueChange = { date = it },
        )
        Spacer(Modifier.height(Spacing.lg))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            measurementField(weight, { weight = it }, R.string.label_weight, Modifier.weight(1f))
            measurementField(bodyFat, { bodyFat = it }, R.string.label_body_fat, Modifier.weight(1f))
        }
        Spacer(Modifier.height(Spacing.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            measurementField(muscleMass, { muscleMass = it }, R.string.label_muscle_mass, Modifier.weight(1f))
            measurementField(chest, { chest = it }, R.string.label_chest, Modifier.weight(1f))
        }
        Spacer(Modifier.height(Spacing.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            measurementField(armLeft, { armLeft = it }, R.string.label_arm_left, Modifier.weight(1f))
            measurementField(armRight, { armRight = it }, R.string.label_arm_right, Modifier.weight(1f))
        }
        Spacer(Modifier.height(Spacing.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            measurementField(waist, { waist = it }, R.string.label_waist, Modifier.weight(1f))
            measurementField(hips, { hips = it }, R.string.label_hips, Modifier.weight(1f))
        }
        Spacer(Modifier.height(Spacing.xl))
        GradientButton(
            text = stringResource(R.string.action_save),
            onClick = {
                onSave(date, weight, bodyFat, muscleMass, chest, armLeft, armRight, waist, hips)
            },
            loading = isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun AddPrSheet(
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (exercise: String, weight: String, reps: String, date: LocalDate) -> Unit,
) {
    var exercise by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }

    SheetScaffold(
        title = stringResource(R.string.add_pr_title),
        error = error,
        onDismiss = onDismiss,
    ) {
        OutlinedTextField(
            value = exercise,
            onValueChange = { exercise = it },
            label = { Text(stringResource(R.string.label_exercise)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.lg))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text(stringResource(R.string.label_pr_weight)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text(stringResource(R.string.label_reps)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(Spacing.lg))
        DateField(
            label = stringResource(R.string.label_date),
            value = date,
            onValueChange = { date = it },
        )
        Spacer(Modifier.height(Spacing.xl))
        GradientButton(
            text = stringResource(R.string.action_save),
            onClick = { onSave(exercise, weight, reps, date) },
            loading = isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
