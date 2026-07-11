package com.kutluhangul.liftgenius.ui.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.BusinessRepository
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.domain.model.ClientPackage
import com.kutluhangul.liftgenius.domain.model.ClientPr
import com.kutluhangul.liftgenius.domain.model.ClientProgress
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
class ClientDetailViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val businessRepository: BusinessRepository,
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
                    val clientDeferred = async { clientRepository.getClient(clientId) }
                    val packagesDeferred = async { businessRepository.getPackages(clientId) }
                    val progressDeferred = async { clientRepository.getProgress(clientId) }
                    val prsDeferred = async { clientRepository.getPrs(clientId) }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            client = clientDeferred.await(),
                            packages = packagesDeferred.await(),
                            progress = progressDeferred.await(),
                            prs = prsDeferred.await(),
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Müşteri yüklenemedi.") }
            }
        }
    }
}
