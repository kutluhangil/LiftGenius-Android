package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Maps to the shared `exercises` table (CLAUDE.md section 4.1).
 * `reps` and `weight` are Strings on purpose ("10-12", "Max", "Bodyweight", "50 kg").
 */
@Serializable
data class Exercise(
    val id: String,
    @SerialName("day_id") val dayId: String,
    val name: String,
    val category: String? = null,
    val sets: Int = 0,
    val reps: String = "",
    val weight: String? = null,
    val notes: String? = null,
    @SerialName("order_index") val orderIndex: Int = 0,
)
