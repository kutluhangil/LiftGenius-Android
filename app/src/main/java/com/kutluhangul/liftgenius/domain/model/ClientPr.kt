@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `client_prs` table — personal records (CLAUDE.md section 4.1). */
@Serializable
data class ClientPr(
    val id: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("exercise_name") val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val date: Instant,
    @SerialName("created_at") val createdAt: Instant,
)
