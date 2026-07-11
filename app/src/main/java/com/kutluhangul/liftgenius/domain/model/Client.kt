@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `clients` table (CLAUDE.md section 4.1). */
@Serializable
data class Client(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val phone: String? = null,
    val email: String? = null,
    @SerialName("birth_date") val birthDate: Instant? = null,
    val gender: Gender? = null,
    val goal: FitnessGoal? = null,
    val notes: String? = null,
    val status: ClientStatus,
    val weight: Double? = null,
    val height: Double? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("trainer_id") val trainerId: String,
)
