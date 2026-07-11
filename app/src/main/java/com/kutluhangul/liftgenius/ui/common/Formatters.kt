@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.ui.common

import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

/** Locale-aware display formatting (tr-TR). */
object Formatters {

    private val turkish = Locale.forLanguageTag("tr-TR")
    private val zone: ZoneId get() = ZoneId.systemDefault()

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", turkish)
    private val dayMonthFormat = DateTimeFormatter.ofPattern("d MMM", turkish)
    private val fullDateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", turkish)
    private val weekdayShortFormat = DateTimeFormatter.ofPattern("EEE", turkish)

    fun currency(amount: Double): String =
        NumberFormat.getCurrencyInstance(turkish).format(amount)

    fun time(instant: Instant): String =
        timeFormat.withZone(zone).format(instant.toJavaInstant())

    fun dayMonth(instant: Instant): String =
        dayMonthFormat.withZone(zone).format(instant.toJavaInstant())

    fun fullDate(instant: Instant): String =
        fullDateFormat.withZone(zone).format(instant.toJavaInstant())

    fun dayMonth(date: LocalDate): String = dayMonthFormat.format(date)

    fun fullDate(date: LocalDate): String = fullDateFormat.format(date)

    fun weekdayShort(date: LocalDate): String = weekdayShortFormat.format(date)
}
