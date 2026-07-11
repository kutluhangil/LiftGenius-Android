@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.SessionRepository
import com.kutluhangul.liftgenius.domain.model.NewSession
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val clientRepository: ClientRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
        val selectedDate: LocalDate = LocalDate.now(),
        val weekSessions: List<Session> = emptyList(),
        val clientNames: Map<String, String> = emptyMap(),
        val isMutating: Boolean = false,
        val mutationError: String? = null,
        val mutationCompleted: Boolean = false,
    ) {
        /** (id, name) options sorted by name — for the session client picker. */
        val clientOptions: List<Pair<String, String>>
            get() = clientNames.entries.sortedBy { it.value }.map { it.key to it.value }

        val weekDays: List<LocalDate> get() = (0L..6L).map { weekStart.plusDays(it) }

        fun sessionsOn(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): List<Session> =
            weekSessions.filter {
                it.date.toJavaInstant().atZone(zone).toLocalDate() == date
            }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadWeek()
    }

    fun loadWeek() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val zone = ZoneId.systemDefault()
                val weekStart = _uiState.value.weekStart
                val from = weekStart.atStartOfDay(zone).toInstant().toKotlinInstant()
                val to = weekStart.plusDays(7).atStartOfDay(zone).toInstant().toKotlinInstant()
                coroutineScope {
                    val sessionsDeferred = async { sessionRepository.getSessionsBetween(from, to) }
                    val clientsDeferred = async { clientRepository.getClients() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            weekSessions = sessionsDeferred.await(),
                            clientNames = clientsDeferred.await().associate { c -> c.id to c.fullName },
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Takvim yüklenemedi.") }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun shiftWeek(offsetWeeks: Long) {
        _uiState.update {
            val newStart = it.weekStart.plusWeeks(offsetWeeks)
            it.copy(weekStart = newStart, selectedDate = newStart)
        }
        loadWeek()
    }

    fun addSession(
        clientId: String?,
        date: LocalDate,
        time: LocalTime,
        durationText: String,
        title: String,
        notes: String,
    ) {
        if (clientId == null) {
            _uiState.update { it.copy(mutationError = "Müşteri seç.") }
            return
        }
        val duration = durationText.trim().toIntOrNull() ?: 60
        val zone = ZoneId.systemDefault()
        val instant = LocalDateTime.of(date, time).atZone(zone).toInstant().toKotlinInstant()
        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true, mutationError = null) }
            try {
                sessionRepository.addSession(
                    NewSession(
                        clientId = clientId,
                        date = instant,
                        durationMinutes = duration,
                        title = title.trim().ifBlank { null },
                        notes = notes.trim().ifBlank { null },
                    ),
                )
                _uiState.update {
                    it.copy(isMutating = false, mutationCompleted = true, selectedDate = date)
                }
                loadWeek()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isMutating = false, mutationError = e.message ?: "Seans kaydedilemedi.") }
            }
        }
    }

    fun consumeMutation() {
        _uiState.update { it.copy(mutationCompleted = false, mutationError = null) }
    }
}
