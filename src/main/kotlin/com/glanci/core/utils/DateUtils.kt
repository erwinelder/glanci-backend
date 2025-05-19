package com.glanci.core.utils

import kotlinx.datetime.*


fun LocalDate.minusDays(days: Int): LocalDate {
    return minus(days, DateTimeUnit.DAY)
}
fun LocalDate.minusWeeks(weeks: Int): LocalDate {
    return minus(weeks, DateTimeUnit.WEEK)
}
fun LocalDate.minusMonths(months: Int): LocalDate {
    return minus(months, DateTimeUnit.MONTH)
}
fun LocalDate.minusYears(years: Int): LocalDate {
    return minus(years, DateTimeUnit.YEAR)
}
fun LocalDate.plusDays(days: Int): LocalDate {
    return plus(days, DateTimeUnit.DAY)
}
fun LocalDate.plusWeeks(weeks: Int): LocalDate {
    return plus(weeks, DateTimeUnit.WEEK)
}
fun LocalDate.plusMonths(months: Int): LocalDate {
    return plus(months, DateTimeUnit.MONTH)
}
fun LocalDate.plusYears(years: Int): LocalDate {
    return plus(years, DateTimeUnit.YEAR)
}


fun getCurrentLocalDateTime(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.UTC)
}

fun getCurrentTimestamp(): Long {
    return getCurrentLocalDateTime().asTimestamp()
}

fun LocalDateTime.asTimestamp(): Long {
    return toInstant(TimeZone.UTC).epochSeconds
}

fun LocalDateTime.toTimeInMillis(): Long {
    return toInstant(TimeZone.UTC).toEpochMilliseconds()
}
