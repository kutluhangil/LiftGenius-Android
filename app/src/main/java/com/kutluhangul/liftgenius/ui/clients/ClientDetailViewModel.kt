@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.BusinessRepository
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.NutritionRepository
import com.kutluhangul.liftgenius.data.repository.WorkoutRepository
import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.domain.model.ClientPackage
import com.kutluhangul.liftgenius.domain.model.ClientPr
import com.kutluhangul.liftgenius.domain.model.ClientProgress
import com.kutluhangul.liftgenius.domain.model.NewClientPackage
import com.kutluhangul.liftgenius.domain.model.NewClientPr
import com.kutluhangul.liftgenius.domain.model.NewClientProgress
import com.kutluhangul.liftgenius.domain.model.NutritionPlan
import com.kutluhangul.liftgenius.domain.model.PaymentMethod
import com.kutluhangul.liftgenius.domain.model.WorkoutPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val businessRepository: BusinessRepository,
    private val workoutRepository: WorkoutRepository,
    private val nutritionRepository: NutritionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val clientId: String = checkNotNull(savedStateHandle["clientId"]) {
        "clientId navigation argument is required"
    }

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val client: Client? = null,
        val packages: List<ClientPackage> = emptyList(),
        val progress: List<ClientProgress> = emptyList(),
        val prs: List<ClientPr> = emptyList(),
        val workoutPlans: List<WorkoutPlan> = emptyList(),
        val nutritionPlans: List<NutritionPlan> = emptyList(),
        val isMutating: Boolean = false,
        val mutationError: String? = null,
        val mutationCompleted: Boolean = false,
        val deleted: Boolean = false,
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
                refreshData()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Müşteri yüklenemedi.") }
            }
        }
    }

    fun addPackage(
        name: String,
        totalSessionsText: String,
        priceText: String,
        method: PaymentMethod?,
        isPaid: Boolean,
        startDate: LocalDate,
    ) {
        val trimmedName = name.trim()
        val total = totalSessionsText.trim().toIntOrNull()
        val price = priceText.trim().replace(',', '.').toDoubleOrNull()
        if (trimmedName.isBlank() || total == null || total <= 0 || price == null) {
            _uiState.update { it.copy(mutationError = "Paket adı, seans sayısı ve ücret gerekli.") }
            return
        }
        mutate {
            businessRepository.addPackage(
                NewClientPackage(
                    clientId = clientId,
                    name = trimmedName,
                    totalSessions = total,
                    remainingSessions = total,
                    price = price,
                    paymentMethod = method,
                    startDate = startDate.toInstantAtStartOfDay(),
                    isPaid = isPaid,
                ),
            )
        }
    }

    fun addProgress(
        date: LocalDate,
        weight: String,
        bodyFat: String,
        muscleMass: String,
        chest: String,
        armLeft: String,
        armRight: String,
        waist: String,
        hips: String,
    ) {
        val values = listOf(weight, bodyFat, muscleMass, chest, armLeft, armRight, waist, hips)
            .map { it.trim().replace(',', '.').toDoubleOrNull() }
        if (values.all { it == null }) {
            _uiState.update { it.copy(mutationError = "En az bir ölçüm değeri gir.") }
            return
        }
        mutate {
            clientRepository.addProgress(
                NewClientProgress(
                    clientId = clientId,
                    date = date.toInstantAtStartOfDay(),
                    weight = values[0],
                    bodyFat = values[1],
                    muscleMass = values[2],
                    chest = values[3],
                    armLeft = values[4],
                    armRight = values[5],
                    waist = values[6],
                    hips = values[7],
                ),
            )
        }
    }

    fun addPr(exerciseName: String, weightText: String, repsText: String, date: LocalDate) {
        val trimmedExercise = exerciseName.trim()
        val weight = weightText.trim().replace(',', '.').toDoubleOrNull()
        val reps = repsText.trim().toIntOrNull()
        if (trimmedExercise.isBlank() || weight == null || reps == null || reps <= 0) {
            _uiState.update { it.copy(mutationError = "Egzersiz, ağırlık ve tekrar gerekli.") }
            return
        }
        mutate {
            clientRepository.addPr(
                NewClientPr(
                    clientId = clientId,
                    exerciseName = trimmedExercise,
                    weight = weight,
                    reps = reps,
                    date = date.toInstantAtStartOfDay(),
                ),
            )
        }
    }

    fun deleteClient() {
        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true, mutationError = null) }
            try {
                clientRepository.deleteClient(clientId)
                _uiState.update { it.copy(isMutating = false, deleted = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isMutating = false, mutationError = e.message ?: "Müşteri silinemedi.") }
            }
        }
    }

    fun consumeMutation() {
        _uiState.update { it.copy(mutationCompleted = false, mutationError = null) }
    }

    private fun mutate(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true, mutationError = null) }
            try {
                block()
                refreshData()
                _uiState.update { it.copy(isMutating = false, mutationCompleted = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isMutating = false, mutationError = e.message ?: "Kaydedilemedi.") }
            }
        }
    }

    private suspend fun refreshData() {
        coroutineScope {
            val clientDeferred = async { clientRepository.getClient(clientId) }
            val packagesDeferred = async { businessRepository.getPackages(clientId) }
            val progressDeferred = async { clientRepository.getProgress(clientId) }
            val prsDeferred = async { clientRepository.getPrs(clientId) }
            val workoutPlansDeferred = async { workoutRepository.getPlans(clientId) }
            val nutritionPlansDeferred = async { nutritionRepository.getPlans(clientId) }
            _uiState.update {
                it.copy(
                    client = clientDeferred.await(),
                    packages = packagesDeferred.await(),
                    progress = progressDeferred.await(),
                    prs = prsDeferred.await(),
                    workoutPlans = workoutPlansDeferred.await(),
                    nutritionPlans = nutritionPlansDeferred.await(),
                )
            }
        }
    }
}

private fun LocalDate.toInstantAtStartOfDay(): Instant =
    atStartOfDay(ZoneId.systemDefault()).toInstant().toKotlinInstant()
