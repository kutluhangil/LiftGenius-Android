package com.kutluhangul.liftgenius.ui.common

import com.kutluhangul.liftgenius.domain.model.ClientStatus
import com.kutluhangul.liftgenius.domain.model.FitnessGoal
import com.kutluhangul.liftgenius.domain.model.Gender
import com.kutluhangul.liftgenius.domain.model.PaymentMethod
import com.kutluhangul.liftgenius.domain.model.SessionStatus
import com.kutluhangul.liftgenius.domain.model.TrainerRole

// Turkish display labels for the shared enums. Only for UI — the DB strings stay
// exactly as defined in CLAUDE.md section 4.2.

fun FitnessGoal.label(): String = when (this) {
    FitnessGoal.WEIGHT_LOSS -> "Kilo Verme"
    FitnessGoal.FAT_LOSS -> "Yağ Yakımı"
    FitnessGoal.MUSCLE_GAIN -> "Kas Kazanımı"
    FitnessGoal.STRENGTH -> "Güç"
    FitnessGoal.TONING -> "Sıkılaşma"
    FitnessGoal.GENERAL_HEALTH -> "Genel Sağlık"
    FitnessGoal.FLEXIBILITY -> "Esneklik"
    FitnessGoal.ENDURANCE -> "Dayanıklılık"
    FitnessGoal.ATHLETIC_PERFORMANCE -> "Atletik Performans"
    FitnessGoal.REHABILITATION -> "Rehabilitasyon"
    FitnessGoal.POSTURE -> "Postür"
    FitnessGoal.WEIGHT_GAIN -> "Kilo Alma"
}

fun Gender.label(): String = when (this) {
    Gender.MALE -> "Erkek"
    Gender.FEMALE -> "Kadın"
    Gender.OTHER -> "Diğer"
}

fun ClientStatus.label(): String = when (this) {
    ClientStatus.ACTIVE -> "Aktif"
    ClientStatus.INACTIVE -> "Pasif"
    ClientStatus.TRIAL -> "Deneme"
    ClientStatus.FROZEN -> "Dondurulmuş"
}

fun SessionStatus.label(): String = when (this) {
    SessionStatus.SCHEDULED -> "Planlandı"
    SessionStatus.COMPLETED -> "Tamamlandı"
    SessionStatus.CANCELLED -> "İptal"
    SessionStatus.NO_SHOW -> "Gelmedi"
}

fun PaymentMethod.label(): String = when (this) {
    PaymentMethod.CASH -> "Nakit"
    PaymentMethod.TRANSFER -> "Havale"
    PaymentMethod.CREDIT_CARD -> "Kredi Kartı"
}

fun TrainerRole.label(): String = when (this) {
    TrainerRole.OWNER -> "Salon Sahibi"
    TrainerRole.TRAINER -> "Eğitmen"
}
