@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `workout_plans` table (CLAUDE.md section 4.1). */
@Serializable
data class WorkoutPlan(
    val id: String,
    @SerialName("client_id") val clientId: String,
    val title: String,
    val description: String? = null,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("trainer_id") val trainerId: String,
)
