@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `nutrition_plans` table (CLAUDE.md section 4.1). */
@Serializable
data class NutritionPlan(
    val id: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("daily_calories") val dailyCalories: Int,
    @SerialName("protein_grams") val proteinGrams: Int,
    @SerialName("carb_grams") val carbGrams: Int,
    @SerialName("fat_grams") val fatGrams: Int,
    @SerialName("meal_plan_text") val mealPlanText: String,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("trainer_id") val trainerId: String,
)
