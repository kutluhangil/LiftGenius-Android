package com.kutluhangul.liftgenius.ui.plans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.WorkoutRepository
import com.kutluhangul.liftgenius.domain.model.Exercise
import com.kutluhangul.liftgenius.domain.model.WorkoutDay
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
import javax.inject.Inject

@HiltViewModel
class WorkoutPlanViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val planId: String = checkNotNull(savedStateHandle["planId"]) {
        "planId navigation argument is required"
    }

    data class DayWithExercises(
        val day: WorkoutDay,
        val exercises: List<Exercise>,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val plan: WorkoutPlan? = null,
        val days: List<DayWithExercises> = emptyList(),
        val clientName: String? = null,
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
                coroutineScope {
                    val planDeferred = async { workoutRepository.getPlan(planId) }
                    val days = workoutRepository.getDays(planId)
                    val daysWithExercises = days.map { day ->
                        async { DayWithExercises(day, workoutRepository.getExercises(day.id)) }
                    }.map { it.await() }
                    val plan = planDeferred.await()
                    val clientName = runCatching { clientRepository.getClient(plan.clientId).fullName }
                        .getOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            plan = plan,
                            days = daysWithExercises,
                            clientName = clientName,
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Plan yüklenemedi.") }
            }
        }
    }
}
