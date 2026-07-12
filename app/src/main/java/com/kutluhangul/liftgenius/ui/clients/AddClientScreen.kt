package com.kutluhangul.liftgenius.ui.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.FitnessGoal
import com.kutluhangul.liftgenius.domain.model.Gender
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.components.FormGroup
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.LabeledDropdown
import com.kutluhangul.liftgenius.ui.components.LabeledField
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun AddClientScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddClientViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var goal by remember { mutableStateOf(FitnessGoal.GENERAL_HEALTH) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hips by remember { mutableStateOf("") }
    var armLeft by remember { mutableStateOf("") }
    var armRight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CancelPill(onClick = onBack)
            }
            Text(
                text = stringResource(R.string.clients_add_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.add_client_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
            Spacer(Modifier.height(Spacing.xl))

            FormGroup(title = stringResource(R.string.client_personal_info)) {
                LabeledField(
                    icon = Icons.Filled.Person,
                    label = stringResource(R.string.label_full_name),
                    value = fullName,
                    onValueChange = { fullName = it; viewModel.clearError() },
                    placeholder = "Örn. Ahmet Yılmaz",
                    modifier = Modifier.fillMaxWidth(),
                )
                LabeledField(
                    icon = Icons.Filled.Phone,
                    label = stringResource(R.string.label_phone),
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "Örn. 0555 555 5555",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth(),
                )
                LabeledField(
                    icon = Icons.Filled.Email,
                    label = stringResource(R.string.label_email),
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Örn. ahmet@email.com",
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.fillMaxWidth(),
                )
                LabeledDropdown(
                    icon = Icons.Filled.Wc,
                    label = stringResource(R.string.label_gender),
                    options = Gender.entries,
                    selected = gender,
                    onSelect = { gender = it },
                    optionLabel = { it.label() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(Spacing.lg))

            FormGroup(title = stringResource(R.string.client_physical_goals)) {
                LabeledDropdown(
                    icon = Icons.Filled.Flag,
                    label = stringResource(R.string.label_goal),
                    options = FitnessGoal.entries,
                    selected = goal,
                    onSelect = { goal = it },
                    optionLabel = { it.label() },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    LabeledField(
                        icon = Icons.Filled.MonitorWeight,
                        label = stringResource(R.string.label_weight),
                        value = weight,
                        onValueChange = { weight = it },
                        placeholder = "0",
                        keyboardType = KeyboardType.Decimal,
                        suffix = "kg",
                        modifier = Modifier.weight(1f),
                    )
                    LabeledField(
                        icon = Icons.Filled.Height,
                        label = stringResource(R.string.label_height),
                        value = height,
                        onValueChange = { height = it },
                        placeholder = "0",
                        keyboardType = KeyboardType.Decimal,
                        suffix = "cm",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(Spacing.lg))

            FormGroup(
                title = stringResource(R.string.client_body_measurements),
                subtitle = stringResource(R.string.client_body_measurements_hint),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    MeasurementField(bodyFat, { bodyFat = it }, R.string.label_body_fat, "%", Icons.Filled.Opacity, Modifier.weight(1f))
                    MeasurementField(muscle, { muscle = it }, R.string.label_muscle_mass, "kg", Icons.Filled.FitnessCenter, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    MeasurementField(chest, { chest = it }, R.string.label_chest, "cm", Icons.Filled.Straighten, Modifier.weight(1f))
                    MeasurementField(waist, { waist = it }, R.string.label_waist, "cm", Icons.Filled.Straighten, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    MeasurementField(armLeft, { armLeft = it }, R.string.label_arm_left, "cm", Icons.Filled.Straighten, Modifier.weight(1f))
                    MeasurementField(armRight, { armRight = it }, R.string.label_arm_right, "cm", Icons.Filled.Straighten, Modifier.weight(1f))
                }
                MeasurementField(hips, { hips = it }, R.string.label_hips, "cm", Icons.Filled.Straighten, Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(Spacing.lg))

            FormGroup(title = stringResource(R.string.label_notes)) {
                LabeledField(
                    icon = Icons.Filled.Notes,
                    label = stringResource(R.string.label_notes),
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Müşteri ile ilgili özel notlarınız (Sakatlık, hastalık vs.)...",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            uiState.error?.let { message ->
                Spacer(Modifier.height(Spacing.lg))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(Spacing.xl))
            GradientButton(
                text = stringResource(R.string.client_save),
                onClick = {
                    viewModel.save(
                        fullName = fullName,
                        phone = phone,
                        email = email,
                        gender = gender,
                        goal = goal,
                        weight = weight,
                        height = height,
                        bodyFat = bodyFat,
                        muscleMass = muscle,
                        chest = chest,
                        armLeft = armLeft,
                        armRight = armRight,
                        waist = waist,
                        hips = hips,
                        notes = notes,
                    )
                },
                loading = uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun MeasurementField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: Int,
    suffix: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    LabeledField(
        icon = icon,
        label = stringResource(labelRes),
        value = value,
        onValueChange = onValueChange,
        placeholder = "0",
        keyboardType = KeyboardType.Decimal,
        suffix = suffix,
        modifier = modifier,
    )
}

/** Pill-shaped "İptal" button for form top bars — glass surface, close icon + label. */
@Composable
private fun CancelPill(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.extended.cardBorder),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = MaterialTheme.extended.textSecondary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.action_cancel),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.extended.textSecondary,
            )
        }
    }
}
