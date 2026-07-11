package com.kutluhangul.liftgenius.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Parsed shapes of Gemini's JSON output. These are drafts — they only touch the shared
// Supabase tables after the trainer confirms saving (CLAUDE.md section 6).

@Serializable
data class GeneratedWorkout(
    val title: String,
    val description: String? = null,
    val days: List<GeneratedWorkoutDay> = emptyList(),
)

@Serializable
data class GeneratedWorkoutDay(
    @SerialName("day_name") val dayName: String,
    val exercises: List<GeneratedExercise> = emptyList(),
)

@Serializable
data class GeneratedExercise(
    val name: String,
    val category: String? = null,
    val sets: Int = 3,
    val reps: String = "10-12",
    val weight: String? = null,
    val notes: String? = null,
)

@Serializable
data class GeneratedNutrition(
    @SerialName("daily_calories") val dailyCalories: Int,
    @SerialName("protein_grams") val proteinGrams: Int,
    @SerialName("carb_grams") val carbGrams: Int,
    @SerialName("fat_grams") val fatGrams: Int,
    @SerialName("meal_plan_text") val mealPlanText: String,
    val notes: String? = null,
)
