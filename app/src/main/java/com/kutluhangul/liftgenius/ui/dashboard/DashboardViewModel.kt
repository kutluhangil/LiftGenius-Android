@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.BusinessRepository
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.SessionRepository
import com.kutluhangul.liftgenius.domain.model.ClientStatus
import com.kutluhangul.liftgenius.domain.model.Session
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
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val sessionRepository: SessionRepository,
    private val businessRepository: BusinessRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val trainerName: String = "",
        val activeClientCount: Int = 0,
        val todaySessions: List<Session> = emptyList(),
        val clientNames: Map<String, String> = emptyMap(),
        val monthRevenue: Double = 0.0,
        val unpaidTotal: Double = 0.0,
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
                val zone = ZoneId.systemDefault()
                val today = LocalDate.now(zone)
                val dayStart = today.atStartOfDay(zone).toInstant().toKotlinInstant()
                val dayEnd = today.plusDays(1).atStartOfDay(zone).toInstant().toKotlinInstant()
                val currentMonth = YearMonth.now(zone)

                coroutineScope {
                    val clientsDeferred = async { clientRepository.getClients() }
                    val sessionsDeferred = async { sessionRepository.getSessionsBetween(dayStart, dayEnd) }
                    val packagesDeferred = async { businessRepository.getAllPackages() }

                    val clients = clientsDeferred.await()
                    val sessions = sessionsDeferred.await()
                    val packages = packagesDeferred.await()

                    val monthRevenue = packages
                        .filter { it.isPaid }
                        .filter {
                            YearMonth.from(it.startDate.toJavaInstant().atZone(zone)) == currentMonth
                        }
                        .sumOf { it.price }
                    val unpaidTotal = packages.filterNot { it.isPaid }.sumOf { it.price }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeClientCount = clients.count { c -> c.status == ClientStatus.ACTIVE },
                            todaySessions = sessions,
                            clientNames = clients.associate { c -> c.id to c.fullName },
                            monthRevenue = monthRevenue,
                            unpaidTotal = unpaidTotal,
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Panel yüklenemedi.") }
            }
        }
    }
}
