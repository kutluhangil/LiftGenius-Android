@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Insert payloads for the shared tables (CLAUDE.md section 4.1).
// `id` is generated client-side (UUID); `created_at` comes from the DB default (now()).
// `trainer_id` fields are filled by the repositories from the authenticated user.

private fun newUuid(): String = UUID.randomUUID().toString()

@Serializable
data class NewClient(
    val id: String = newUuid(),
    @SerialName("full_name") val fullName: String,
    val phone: String? = null,
    val email: String? = null,
    @SerialName("birth_date") val birthDate: Instant? = null,
    val gender: Gender? = null,
    val goal: FitnessGoal? = null,
    val notes: String? = null,
    val status: ClientStatus = ClientStatus.ACTIVE,
    val weight: Double? = null,
    val height: Double? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("trainer_id") val trainerId: String = "",
)

@Serializable
data class NewClientProgress(
    val id: String = newUuid(),
    @SerialName("client_id") val clientId: String,
    val date: Instant,
    val weight: Double? = null,
    @SerialName("body_fat") val bodyFat: Double? = null,
    @SerialName("muscle_mass") val muscleMass: Double? = null,
    val chest: Double? = null,
    @SerialName("arm_left") val armLeft: Double? = null,
    @SerialName("arm_right") val armRight: Double? = null,
    val waist: Double? = null,
    val hips: Double? = null,
)

@Serializable
data class NewClientPr(
    val id: String = newUuid(),
    @SerialName("client_id") val clientId: String,
    @SerialName("exercise_name") val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val date: Instant,
)

@Serializable
data class NewClientPackage(
    val id: String = newUuid(),
    @SerialName("client_id") val clientId: String,
    val name: String,
    @SerialName("total_sessions") val totalSessions: Int,
    @SerialName("remaining_sessions") val remainingSessions: Int,
    val price: Double,
    @SerialName("payment_method") val paymentMethod: PaymentMethod? = null,
    @SerialName("start_date") val startDate: Instant,
    @SerialName("end_date") val endDate: Instant? = null,
    @SerialName("is_paid") val isPaid: Boolean = false,
)

@Serializable
data class NewSession(
    val id: String = newUuid(),
    @SerialName("client_id") val clientId: String,
    @SerialName("package_id") val packageId: String? = null,
    val date: Instant,
    @SerialName("duration_minutes") val durationMinutes: Int,
    val title: String? = null,
    val status: SessionStatus = SessionStatus.SCHEDULED,
    val notes: String? = null,
)

@Serializable
data class NewNutritionPlan(
    val id: String = newUuid(),
    @SerialName("client_id") val clientId: String,
    @SerialName("daily_calories") val dailyCalories: Int,
    @SerialName("protein_grams") val proteinGrams: Int,
    @SerialName("carb_grams") val carbGrams: Int,
    @SerialName("fat_grams") val fatGrams: Int,
    @SerialName("meal_plan_text") val mealPlanText: String,
    val notes: String? = null,
    @SerialName("trainer_id") val trainerId: String = "",
)

@Serializable
data class NewWorkoutPlan(
    val id: String = newUuid(),
    @SerialName("client_id") val clientId: String,
    val title: String,
    val description: String? = null,
    @SerialName("trainer_id") val trainerId: String = "",
)

@Serializable
data class NewWorkoutDay(
    val id: String = newUuid(),
    @SerialName("plan_id") val planId: String,
    @SerialName("day_name") val dayName: String,
    @SerialName("order_index") val orderIndex: Int = 0,
)

@Serializable
data class NewExercise(
    val id: String = newUuid(),
    @SerialName("day_id") val dayId: String,
    val name: String,
    val category: String? = null,
    val sets: Int = 0,
    val reps: String = "",
    val weight: String? = null,
    val notes: String? = null,
    @SerialName("order_index") val orderIndex: Int = 0,
)
