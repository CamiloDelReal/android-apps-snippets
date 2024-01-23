package package.core.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter


fun LocalTime.parseToString(format: String? = null, use24Hour: Boolean = true): String {
    val pattern = format ?: if(use24Hour) {
            Constants.TIME_PATTERN_24H
        } else {
            Constants.TIME_PATTERN
        }
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}

fun LocalDate.parseToString(format: String? = null): String {
    val pattern = format ?: Constants.DATE_PATTERN_DB
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}

fun LocalDateTime.parseToString(format: String? = null, use24Hour: Boolean = true): String {
    val pattern = format ?: if(use24Hour) {
        Constants.DATE_TIME_24H_PATTERN_DB
    } else {
        Constants.DATE_TIME_PATTERN_DB
    }
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}

fun String.parseToLocalDate(format: String? = null): LocalDate {
    val pattern = format ?: Constants.DATE_PATTERN_DB
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return LocalDate.parse(this, formatter)
}

fun String.parseToLocalDateTime(format: String? = null, use24Hour: Boolean = true): LocalDateTime {
    val pattern = format ?: if(use24Hour) {
        Constants.DATE_TIME_24H_PATTERN_DB
    } else {
        Constants.DATE_TIME_PATTERN_DB
    }
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return LocalDateTime.parse(this, formatter)
}

fun YearMonth.parseToString(format: String? = null): String {
    val pattern = format ?: Constants.YEAR_MONTH_PATTERN
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}