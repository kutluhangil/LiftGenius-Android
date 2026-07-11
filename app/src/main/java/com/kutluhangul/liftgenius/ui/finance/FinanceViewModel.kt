package com.kutluhangul.liftgenius.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.BusinessRepository
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.domain.model.ClientPackage
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
class FinanceViewModel @Inject constructor(
    private val businessRepository: BusinessRepository,
    private val clientRepository: ClientRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val packages: List<ClientPackage> = emptyList(),
        val clientNames: Map<String, String> = emptyMap(),
    ) {
        val paidTotal: Double get() = packages.filter { it.isPaid }.sumOf { it.price }
        val unpaidTotal: Double get() = packages.filterNot { it.isPaid }.sumOf { it.price }
    }

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
                    val packagesDeferred = async { businessRepository.getAllPackages() }
                    val clientsDeferred = async { clientRepository.getClients() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            packages = packagesDeferred.await(),
                            clientNames = clientsDeferred.await().associate { c -> c.id to c.fullName },
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Finans verileri yüklenemedi.") }
            }
        }
    }
}
