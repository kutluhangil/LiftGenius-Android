@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.domain.model.FitnessGoal
import com.kutluhangul.liftgenius.domain.model.NewClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@HiltViewModel
class AddClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
) : ViewModel() {

    data class UiState(
        val isSaving: Boolean = false,
        val error: String? = null,
        val saved: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun save(
        fullName: String,
        phone: String,
        email: String,
        goal: FitnessGoal?,
        weight: String,
        height: String,
        notes: String,
    ) {
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(error = "Ad Soyad gerekli.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                clientRepository.addClient(
                    NewClient(
                        fullName = trimmedName,
                        phone = phone.trim().ifBlank { null },
                        email = email.trim().ifBlank { null },
                        goal = goal,
                        weight = weight.trim().replace(',', '.').toDoubleOrNull(),
                        height = height.trim().replace(',', '.').toDoubleOrNull(),
                        notes = notes.trim().ifBlank { null },
                    ),
                )
                _uiState.update { it.copy(isSaving = false, saved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Müşteri kaydedilemedi.") }
            }
        }
    }

    fun clearError() {
        if (_uiState.value.error != null) {
            _uiState.update { it.copy(error = null) }
        }
    }
}
