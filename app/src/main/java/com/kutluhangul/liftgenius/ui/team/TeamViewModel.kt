@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.AuthRepository
import com.kutluhangul.liftgenius.data.repository.BusinessRepository
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.SessionRepository
import com.kutluhangul.liftgenius.domain.model.TrainerProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val businessRepository: BusinessRepository,
    private val clientRepository: ClientRepository,
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val members: List<TrainerProfile> = emptyList(),
        val currentUserId: String? = null,
        val salonName: String? = null,
        val clientCount: Int = 0,
        val monthSessions: Int = 0,
        val monthRevenue: Double = 0.0,
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
                val month = YearMonth.now(zone)
                val monthStart = month.atDay(1).atStartOfDay(zone).toInstant().toKotlinInstant()
                val monthEnd = month.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toKotlinInstant()

                coroutineScope {
                    val membersDeferred = async { businessRepository.getTeam() }
                    val profileDeferred = async { runCatching { businessRepository.getOwnProfile() }.getOrNull() }
                    val clientsDeferred = async { clientRepository.getClients() }
                    val sessionsDeferred = async { sessionRepository.getSessionsBetween(monthStart, monthEnd) }
                    val packagesDeferred = async { businessRepository.getAllPackages() }

                    val packages = packagesDeferred.await()
                    val monthRevenue = packages.filter { it.isPaid }
                        .filter { YearMonth.from(it.startDate.toJavaInstant().atZone(zone)) == month }
                        .sumOf { it.price }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            members = membersDeferred.await(),
                            currentUserId = authRepository.currentUserId(),
                            salonName = profileDeferred.await()?.salonName,
                            clientCount = clientsDeferred.await().size,
                            monthSessions = sessionsDeferred.await().size,
                            monthRevenue = monthRevenue,
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Takım yüklenemedi.") }
            }
        }
    }
}
