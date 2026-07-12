@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.BusinessRepository
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.SessionRepository
import com.kutluhangul.liftgenius.domain.model.ClientPackage
import com.kutluhangul.liftgenius.domain.model.SessionStatus
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
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val businessRepository: BusinessRepository,
    private val clientRepository: ClientRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val packages: List<ClientPackage> = emptyList(),
        val clientNames: Map<String, String> = emptyMap(),
        val sixMonthValues: List<Double> = List(6) { 0.0 },
        val sixMonthLabels: List<String> = emptyList(),
        val monthStreak: Int = 0,
        val completedThisMonth: Int = 0,
        val revenueGoal: Double = 50_000.0,
        val sessionGoal: Int = 40,
    ) {
        val paidTotal: Double get() = packages.filter { it.isPaid }.sumOf { it.price }
        val unpaidTotal: Double get() = packages.filterNot { it.isPaid }.sumOf { it.price }
        val totalRevenue: Double get() = packages.sumOf { it.price }
        val paidCount: Int get() = packages.count { it.isPaid }
        val unpaidCount: Int get() = packages.count { !it.isPaid }
        val activePackages: Int get() = packages.count { it.remainingSessions > 0 }
        val averagePackage: Double get() = if (packages.isEmpty()) 0.0 else totalRevenue / packages.size
        val collectedFraction: Float
            get() = if (totalRevenue <= 0.0) 0f else (paidTotal / totalRevenue).toFloat()
        val revenueFraction: Float
            get() = if (revenueGoal <= 0.0) 0f else (paidTotal / revenueGoal).toFloat().coerceIn(0f, 1f)
        val sessionFraction: Float
            get() = if (sessionGoal <= 0) 0f else (completedThisMonth.toFloat() / sessionGoal).coerceIn(0f, 1f)
        val unpaidPackages: List<ClientPackage> get() = packages.filterNot { it.isPaid }
    }

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
                val currentMonth = YearMonth.now(zone)
                val monthStartInstant = currentMonth.atDay(1).atStartOfDay(zone).toInstant().toKotlinInstant()
                val monthEndInstant = currentMonth.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toKotlinInstant()

                coroutineScope {
                    val packagesDeferred = async { businessRepository.getAllPackages() }
                    val clientsDeferred = async { clientRepository.getClients() }
                    val monthSessionsDeferred = async {
                        sessionRepository.getSessionsBetween(monthStartInstant, monthEndInstant)
                    }

                    val packages = packagesDeferred.await()
                    val clients = clientsDeferred.await()
                    val monthSessions = monthSessionsDeferred.await()

                    val months = (5 downTo 0).map { currentMonth.minusMonths(it.toLong()) }
                    val sixMonthValues = months.map { month ->
                        packages.filter { it.isPaid }
                            .filter { YearMonth.from(it.startDate.toJavaInstant().atZone(zone)) == month }
                            .sumOf { it.price }
                    }
                    val sixMonthLabels = months.map {
                        it.month.getDisplayName(TextStyle.SHORT, turkish).replaceFirstChar { c -> c.uppercase() }
                    }
                    // Consecutive months (ending this month) that had paid revenue.
                    var streak = 0
                    for (value in sixMonthValues.reversed()) {
                        if (value > 0.0) streak++ else break
                    }
                    val completedThisMonth = monthSessions.count { it.status == SessionStatus.COMPLETED }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            packages = packages,
                            clientNames = clients.associate { c -> c.id to c.fullName },
                            sixMonthValues = sixMonthValues,
                            sixMonthLabels = sixMonthLabels,
                            monthStreak = streak,
                            completedThisMonth = completedThisMonth,
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
