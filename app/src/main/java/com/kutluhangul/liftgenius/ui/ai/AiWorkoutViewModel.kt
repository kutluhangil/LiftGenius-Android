package com.kutluhangul.liftgenius.ui.ai

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.remote.GeminiService
import com.kutluhangul.liftgenius.data.remote.GeneratedWorkout
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.WorkoutRepository
import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.domain.model.NewExercise
import com.kutluhangul.liftgenius.domain.model.NewWorkoutDay
import com.kutluhangul.liftgenius.domain.model.NewWorkoutPlan
import com.kutluhangul.liftgenius.ui.common.label
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AiWorkoutViewModel @Inject constructor(
    private val geminiService: GeminiService,
    private val clientRepository: ClientRepository,
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val clientId: String = checkNotNull(savedStateHandle["clientId"]) {
        "clientId navigation argument is required"
    }

    private val json = Json { ignoreUnknownKeys = true }

    data class UiState(
        val client: Client? = null,
        val clientError: String? = null,
        val isGenerating: Boolean = false,
        val generateError: String? = null,
        val result: GeneratedWorkout? = null,
        val isSaving: Boolean = false,
        val saved: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(client = clientRepository.getClient(clientId)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Client context only enriches the prompt; surface but don't block.
                _uiState.update { it.copy(clientError = e.message) }
            }
        }
    }

    fun generate(daysPerWeek: Int, level: String, equipment: String, preferences: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, generateError = null) }
            try {
                val text = geminiService.generate(
                    buildPrompt(daysPerWeek, level, equipment, preferences),
                )
                val parsed = json.decodeFromString<GeneratedWorkout>(
                    GeminiService.extractJson(text),
                )
                if (parsed.days.isEmpty()) {
                    error("Gemini gün listesi döndürmedi — tekrar dene.")
                }
                _uiState.update { it.copy(isGenerating = false, result = parsed) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isGenerating = false, generateError = e.message ?: "Üretim başarısız.")
                }
            }
        }
    }

    fun save() {
        val result = _uiState.value.result ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, generateError = null) }
            try {
                val plan = workoutRepository.addPlan(
                    NewWorkoutPlan(
                        clientId = clientId,
                        title = result.title,
                        description = result.description,
                    ),
                )
                result.days.forEachIndexed { dayIndex, day ->
                    val savedDay = workoutRepository.addDay(
                        NewWorkoutDay(
                            planId = plan.id,
                            dayName = day.dayName,
                            orderIndex = dayIndex,
                        ),
                    )
                    if (day.exercises.isNotEmpty()) {
                        workoutRepository.addExercises(
                            day.exercises.mapIndexed { exerciseIndex, exercise ->
                                NewExercise(
                                    dayId = savedDay.id,
                                    name = exercise.name,
                                    category = exercise.category,
                                    sets = exercise.sets,
                                    reps = exercise.reps,
                                    weight = exercise.weight,
                                    notes = exercise.notes,
                                    orderIndex = exerciseIndex,
                                )
                            },
                        )
                    }
                }
                _uiState.update { it.copy(isSaving = false, saved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, generateError = e.message ?: "Plan kaydedilemedi.")
                }
            }
        }
    }

    fun regenerate() {
        _uiState.update { it.copy(result = null, generateError = null) }
    }

    private fun buildPrompt(
        daysPerWeek: Int,
        level: String,
        equipment: String,
        preferences: String,
    ): String {
        val client = _uiState.value.client
        val clientContext = client?.let {
            buildString {
                append("Danışan: ${it.fullName}.")
                it.goal?.let { goal -> append(" Hedef: ${goal.label()}.") }
                it.weight?.let { w -> append(" Kilo: $w kg.") }
                it.height?.let { h -> append(" Boy: $h cm.") }
                it.notes?.let { n -> append(" Not: $n.") }
            }
        }.orEmpty()

        return """
            Sen deneyimli bir personal trainer'sın. Haftalık antrenman programı hazırla.
            $clientContext
            Haftada $daysPerWeek antrenman günü. Seviye: $level.
            ${if (equipment.isNotBlank()) "Ekipman: $equipment." else ""}
            ${if (preferences.isNotBlank()) "Tercihler: $preferences." else ""}
            Egzersiz ve gün isimleri Türkçe olsun.
            Yanıt olarak SADECE aşağıdaki şemaya uyan geçerli bir JSON nesnesi döndür, başka hiçbir metin ekleme:
            {"title":"Program adı","description":"Kısa açıklama","days":[{"day_name":"Gün adı (örn. Pazartesi - İtiş)","exercises":[{"name":"Egzersiz adı","category":"Kas grubu","sets":4,"reps":"8-12","weight":"Öneri veya null","notes":"İpucu veya null"}]}]}
        """.trimIndent()
    }
}
