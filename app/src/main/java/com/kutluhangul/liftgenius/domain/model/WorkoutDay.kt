package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Maps to the shared `workout_days` table (CLAUDE.md section 4.1). */
@Serializable
data class WorkoutDay(
    val id: String,
    @SerialName("plan_id") val planId: String,
    @SerialName("day_name") val dayName: String,
    @SerialName("order_index") val orderIndex: Int = 0,
)
