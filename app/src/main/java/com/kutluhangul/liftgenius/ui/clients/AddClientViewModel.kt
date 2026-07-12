@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.domain.model.FitnessGoal
import com.kutluhangul.liftgenius.domain.model.Gender
import com.kutluhangul.liftgenius.domain.model.NewClient
import com.kutluhangul.liftgenius.domain.model.NewClientProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

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

    @Suppress("LongParameterList")
    fun save(
        fullName: String,
        phone: String,
        email: String,
        gender: Gender,
        goal: FitnessGoal?,
        weight: String,
        height: String,
        bodyFat: String,
        muscleMass: String,
        chest: String,
        armLeft: String,
        armRight: String,
        waist: String,
        hips: String,
        notes: String,
    ) {
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(error = "Ad Soyad gerekli.") }
            return
        }
        fun parse(value: String) = value.trim().replace(',', '.').toDoubleOrNull()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val client = clientRepository.addClient(
                    NewClient(
                        fullName = trimmedName,
                        phone = phone.trim().ifBlank { null },
                        email = email.trim().ifBlank { null },
                        gender = gender,
                        goal = goal,
                        weight = parse(weight),
                        height = parse(height),
                        notes = notes.trim().ifBlank { null },
                    ),
                )
                // Starting body measurements are saved as the first progress record (iOS parity).
                val measurements = listOf(
                    parse(weight), parse(bodyFat), parse(muscleMass), parse(chest),
                    parse(armLeft), parse(armRight), parse(waist), parse(hips),
                )
                if (measurements.any { it != null }) {
                    clientRepository.addProgress(
                        NewClientProgress(
                            clientId = client.id,
                            date = java.time.Instant.now().toKotlinInstant(),
                            weight = parse(weight),
                            bodyFat = parse(bodyFat),
                            muscleMass = parse(muscleMass),
                            chest = parse(chest),
                            armLeft = parse(armLeft),
                            armRight = parse(armRight),
                            waist = parse(waist),
                            hips = parse(hips),
                        ),
                    )
                }
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
