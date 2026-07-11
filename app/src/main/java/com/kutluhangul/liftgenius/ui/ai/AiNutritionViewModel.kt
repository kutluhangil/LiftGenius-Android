package com.kutluhangul.liftgenius.ui.ai

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.remote.GeminiService
import com.kutluhangul.liftgenius.data.remote.GeneratedNutrition
import com.kutluhangul.liftgenius.data.repository.ClientRepository
import com.kutluhangul.liftgenius.data.repository.NutritionRepository
import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.domain.model.NewNutritionPlan
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
class AiNutritionViewModel @Inject constructor(
    private val geminiService: GeminiService,
    private val clientRepository: ClientRepository,
    private val nutritionRepository: NutritionRepository,
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
        val result: GeneratedNutrition? = null,
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
                _uiState.update { it.copy(clientError = e.message) }
            }
        }
    }

    fun generate(targetCalories: String, mealsPerDay: Int, preferences: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, generateError = null) }
            try {
                val text = geminiService.generate(
                    buildPrompt(targetCalories.trim().toIntOrNull(), mealsPerDay, preferences),
                )
                val parsed = json.decodeFromString<GeneratedNutrition>(
                    GeminiService.extractJson(text),
                )
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
                nutritionRepository.addPlan(
                    NewNutritionPlan(
                        clientId = clientId,
                        dailyCalories = result.dailyCalories,
                        proteinGrams = result.proteinGrams,
                        carbGrams = result.carbGrams,
                        fatGrams = result.fatGrams,
                        mealPlanText = result.mealPlanText,
                        notes = result.notes,
                    ),
                )
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

    private fun buildPrompt(targetCalories: Int?, mealsPerDay: Int, preferences: String): String {
        val client = _uiState.value.client
        val clientContext = client?.let {
            buildString {
                append("Danışan: ${it.fullName}.")
                it.goal?.let { goal -> append(" Hedef: ${goal.label()}.") }
                it.weight?.let { w -> append(" Kilo: $w kg.") }
                it.height?.let { h -> append(" Boy: $h cm.") }
                it.gender?.let { g -> append(" Cinsiyet: ${g.name.lowercase()}.") }
            }
        }.orEmpty()

        return """
            Sen deneyimli bir spor beslenmesi uzmanısın. Günlük beslenme planı hazırla.
            $clientContext
            ${targetCalories?.let { "Hedef kalori: $it kcal." } ?: "Kalori hedefini danışana göre sen belirle."}
            Günde $mealsPerDay öğün.
            ${if (preferences.isNotBlank()) "Tercihler: $preferences." else ""}
            Öğün planı metni Türkçe olsun; her öğünü ayrı satırda, besinler ve gram miktarlarıyla yaz.
            Yanıt olarak SADECE aşağıdaki şemaya uyan geçerli bir JSON nesnesi döndür, başka hiçbir metin ekleme:
            {"daily_calories":2200,"protein_grams":160,"carb_grams":220,"fat_grams":70,"meal_plan_text":"Kahvaltı: ...\nÖğle: ...","notes":"Ek öneriler veya null"}
        """.trimIndent()
    }
}
