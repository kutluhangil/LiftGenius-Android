@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.BusinessRepository
import com.kutluhangul.liftgenius.domain.model.TrainerProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val businessRepository: BusinessRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val profile: TrainerProfile? = null,
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
                val profile = businessRepository.getOwnProfile()
                _uiState.update { it.copy(isLoading = false, profile = profile) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Profil yüklenemedi.") }
            }
        }
    }

    // Only full_name and salon_name have columns in the shared schema; specialty/phone from
    // the iOS UI are intentionally not persisted (would require a schema change).
    fun save(fullName: String, salonName: String) {
        val current = _uiState.value.profile ?: return
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(saveError = "Ad Soyad gerekli.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            try {
                businessRepository.updateProfile(
                    current.copy(
                        fullName = trimmedName,
                        salonName = salonName.trim().ifBlank { null },
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
