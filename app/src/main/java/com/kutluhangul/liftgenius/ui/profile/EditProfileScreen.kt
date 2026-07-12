package com.kutluhangul.liftgenius.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.TrainerProfile
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.FormGroup
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.LabeledField
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.SubPageScaffold
import com.kutluhangul.liftgenius.ui.theme.Spacing

@Composable
fun EditProfileScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    SubPageScaffold(title = stringResource(R.string.edit_profile_title), onClose = onClose) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.profile == null -> ErrorState(
                message = uiState.error ?: stringResource(R.string.state_error_generic),
                onRetry = viewModel::load,
            )
            else -> EditForm(
                profile = requireNotNull(uiState.profile),
                isSaving = uiState.isSaving,
                saveError = uiState.saveError,
                onFieldChange = viewModel::clearSaveError,
                onSave = viewModel::save,
            )
        }
    }
}

@Composable
private fun EditForm(
    profile: TrainerProfile,
    isSaving: Boolean,
    saveError: String?,
    onFieldChange: () -> Unit,
    onSave: (fullName: String, salonName: String) -> Unit,
) {
    var fullName by remember(profile.id) { mutableStateOf(profile.fullName) }
    var salonName by remember(profile.id) { mutableStateOf(profile.salonName.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
    ) {
        FormGroup(title = stringResource(R.string.edit_profile_title)) {
            LabeledField(
                icon = Icons.Filled.Person,
                label = stringResource(R.string.label_full_name),
                value = fullName,
                onValueChange = { fullName = it; onFieldChange() },
                modifier = Modifier.fillMaxWidth(),
            )
            LabeledField(
                icon = Icons.Filled.Business,
                label = stringResource(R.string.label_business_name),
                value = salonName,
                onValueChange = { salonName = it },
                placeholder = stringResource(R.string.business_name_placeholder),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        saveError?.let { message ->
            Spacer(Modifier.height(Spacing.md))
            Text(text = message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(Spacing.xl))
        GradientButton(
            text = stringResource(R.string.action_save),
            onClick = { onSave(fullName, salonName) },
            loading = isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
