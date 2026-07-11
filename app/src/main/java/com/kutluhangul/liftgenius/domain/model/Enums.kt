package com.kutluhangul.liftgenius.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ⚠️ SHARED BACKEND CONTRACT (CLAUDE.md section 4.2)
// The @SerialName values are stored as strings in Supabase and MUST match iOS exactly.
// Never rename or reorder these values. New values may only be appended.

@Serializable
enum class Gender {
    @SerialName("male") MALE,
    @SerialName("female") FEMALE,
    @SerialName("other") OTHER,
}

@Serializable
enum class ClientStatus {
    @SerialName("active") ACTIVE,
    @SerialName("inactive") INACTIVE,
    @SerialName("trial") TRIAL,
    @SerialName("frozen") FROZEN,
}

@Serializable
enum class FitnessGoal {
    @SerialName("weightLoss") WEIGHT_LOSS,
    @SerialName("fatLoss") FAT_LOSS,
    @SerialName("muscleGain") MUSCLE_GAIN,
    @SerialName("strength") STRENGTH,
    @SerialName("toning") TONING,
    @SerialName("generalHealth") GENERAL_HEALTH,
    @SerialName("flexibility") FLEXIBILITY,
    @SerialName("endurance") ENDURANCE,
    @SerialName("athleticPerformance") ATHLETIC_PERFORMANCE,
    @SerialName("rehabilitation") REHABILITATION,
    @SerialName("posture") POSTURE,
    @SerialName("weightGain") WEIGHT_GAIN,
}

@Serializable
enum class PaymentMethod {
    @SerialName("cash") CASH,
    @SerialName("transfer") TRANSFER,
    @SerialName("creditCard") CREDIT_CARD,
}

@Serializable
enum class SessionStatus {
    @SerialName("scheduled") SCHEDULED,
    @SerialName("completed") COMPLETED,
    @SerialName("cancelled") CANCELLED,
    @SerialName("noShow") NO_SHOW,
}

@Serializable
enum class TrainerRole {
    @SerialName("owner") OWNER,
    @SerialName("trainer") TRAINER,
}
