@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Maps to the shared `packages` table — memberships (CLAUDE.md section 4.1). */
@Serializable
data class ClientPackage(
    val id: String,
    @SerialName("client_id") val clientId: String,
    val name: String,
    @SerialName("total_sessions") val totalSessions: Int,
    @SerialName("remaining_sessions") val remainingSessions: Int,
    val price: Double,
    @SerialName("payment_method") val paymentMethod: PaymentMethod? = null,
    @SerialName("start_date") val startDate: Instant,
    @SerialName("end_date") val endDate: Instant? = null,
    @SerialName("is_paid") val isPaid: Boolean,
    @SerialName("created_at") val createdAt: Instant,
)
