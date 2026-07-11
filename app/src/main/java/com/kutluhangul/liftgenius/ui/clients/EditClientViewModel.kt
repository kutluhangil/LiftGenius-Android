@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.domain.model.ClientStatus
import com.kutluhangul.liftgenius.domain.model.FitnessGoal
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
class EditClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val clientId: String = checkNotNull(savedStateHandle["clientId"]) {
        "clientId navigation argument is required"
    }

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val client: Client? = null,
        val isSaving: Boolean = false,
        val saveError: String? = null,
        val saved: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val client = clientRepository.getClient(clientId)
                _uiState.update { it.copy(isLoading = false, client = client) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Müşteri yüklenemedi.") }
            }
        }
    }

    fun save(
        fullName: String,
        phone: String,
        email: String,
        goal: FitnessGoal?,
        status: ClientStatus,
        weight: String,
        height: String,
        notes: String,
    ) {
        val current = _uiState.value.client ?: return
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(saveError = "Ad Soyad gerekli.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            try {
                clientRepository.updateClient(
                    current.copy(
                        fullName = trimmedName,
                        phone = phone.trim().ifBlank { null },
                        email = email.trim().ifBlank { null },
                        goal = goal,
                        status = status,
                        weight = weight.trim().replace(',', '.').toDoubleOrNull(),
                        height = height.trim().replace(',', '.').toDoubleOrNull(),
                        notes = notes.trim().ifBlank { null },
                    ),
                )
                _uiState.update { it.copy(isSaving = false, saved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, saveError = e.message ?: "Kaydedilemedi.") }
            }
        }
    }

    fun clearSaveError() {
        if (_uiState.value.saveError != null) {
            _uiState.update { it.copy(saveError = null) }
        }
    }
}
