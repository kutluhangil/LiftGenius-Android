package com.kutluhangul.liftgenius.ui.plans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.NutritionRepository
import com.kutluhangul.liftgenius.domain.model.NutritionPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionPlanViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val planId: String = checkNotNull(savedStateHandle["planId"]) {
        "planId navigation argument is required"
    }

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val plan: NutritionPlan? = null,
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
                val plan = nutritionRepository.getPlan(planId)
                val clientName = runCatching { clientRepository.getClient(plan.clientId).fullName }
                    .getOrNull()
                _uiState.update { it.copy(isLoading = false, plan = plan, clientName = clientName) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Plan yüklenemedi.") }
            }
        }
    }
}
