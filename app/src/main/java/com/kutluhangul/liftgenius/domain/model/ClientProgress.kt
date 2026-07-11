@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `client_progress` table (CLAUDE.md section 4.1). */
@Serializable
data class ClientProgress(
    val id: String,
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
    @SerialName("created_at") val createdAt: Instant,
)
