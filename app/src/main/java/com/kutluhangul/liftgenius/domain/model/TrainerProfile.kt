@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `trainer_profiles` table; `id` == auth user id (CLAUDE.md section 4.1). */
@Serializable
data class TrainerProfile(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val role: TrainerRole,
    @SerialName("salon_name") val salonName: String? = null,
    @SerialName("created_at") val createdAt: Instant,
)
