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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
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
        val unpaidCount: Int = 0,
        val finishedPackages: Int = 0,
        val weeklyBars: List<Int> = List(7) { 0 },
        val weeklyLabels: List<String> = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"),
        val monthlyValues: List<Double> = List(4) { 0.0 },
        val monthlyLabels: List<String> = emptyList(),
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val turkish = Locale.forLanguageTag("tr-TR")

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
                val weekStart = today.with(DayOfWeek.MONDAY)
                val weekStartInstant = weekStart.atStartOfDay(zone).toInstant().toKotlinInstant()
                val weekEndInstant = weekStart.plusDays(7).atStartOfDay(zone).toInstant().toKotlinInstant()
                val currentMonth = YearMonth.now(zone)

                coroutineScope {
                    val clientsDeferred = async { clientRepository.getClients() }
                    val todayDeferred = async { sessionRepository.getSessionsBetween(dayStart, dayEnd) }
                    val weekDeferred = async { sessionRepository.getSessionsBetween(weekStartInstant, weekEndInstant) }
                    val packagesDeferred = async { businessRepository.getAllPackages() }

                    val clients = clientsDeferred.await()
                    val todaySessions = todayDeferred.await()
                    val weekSessions = weekDeferred.await()
                    val packages = packagesDeferred.await()

                    // Weekly session bars (Mon..Sun)
                    val weeklyBars = IntArray(7)
                    weekSessions.forEach { session ->
                        val date = session.date.toJavaInstant().atZone(zone).toLocalDate()
                        val idx = (date.dayOfWeek.value - 1).coerceIn(0, 6)
                        weeklyBars[idx]++
                    }

                    // Monthly revenue trend (last 4 months, paid packages)
                    val months = (3 downTo 0).map { currentMonth.minusMonths(it.toLong()) }
                    val monthlyValues = months.map { month ->
                        packages.filter { it.isPaid }
                            .filter { YearMonth.from(it.startDate.toJavaInstant().atZone(zone)) == month }
                            .sumOf { it.price }
                    }
                    val monthlyLabels = months.map {
                        it.month.getDisplayName(TextStyle.SHORT, turkish).replaceFirstChar { c -> c.uppercase() }
                    }

                    val monthRevenue = packages
                        .filter { it.isPaid }
                        .filter { YearMonth.from(it.startDate.toJavaInstant().atZone(zone)) == currentMonth }
                        .sumOf { it.price }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeClientCount = clients.count { c -> c.status == ClientStatus.ACTIVE },
                            todaySessions = todaySessions,
                            clientNames = clients.associate { c -> c.id to c.fullName },
                            monthRevenue = monthRevenue,
                            unpaidCount = packages.count { p -> !p.isPaid },
                            finishedPackages = packages.count { p -> p.remainingSessions <= 0 },
                            weeklyBars = weeklyBars.toList(),
                            monthlyValues = monthlyValues,
                            monthlyLabels = monthlyLabels,
                        )
                    }
                    loadTrainerName()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Panel yüklenemedi.") }
            }
        }
    }

    private fun loadTrainerName() {
        viewModelScope.launch {
            val name = runCatching { businessRepository.getOwnProfile().fullName }.getOrNull()
            if (name != null) _uiState.update { it.copy(trainerName = name) }
        }
    }
}
