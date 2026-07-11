@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `sessions` table (CLAUDE.md section 4.1). */
@Serializable
data class Session(
    val id: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("package_id") val packageId: String? = null,
    val date: Instant,
    @SerialName("duration_minutes") val durationMinutes: Int,
    val title: String? = null,
    val status: SessionStatus,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: Instant,
)
