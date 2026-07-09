package com.nahid.expensetracker.core.utils.extension

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val dateFormatter by lazy {
    DateTimeFormatter.ofPattern("d-M-yyyy", Locale.ENGLISH)
}

private fun String.toLocalDate(): LocalDate =
    LocalDate.parse(this, dateFormatter)

fun String.isOn(date: String): Boolean {
    return runCatching {
        toLocalDate() == date.toLocalDate()
    }.getOrDefault(false)
}

fun String.isAfterDate(date: String): Boolean {
    return runCatching {
        toLocalDate().isAfter(date.toLocalDate())
    }.getOrDefault(false)
}

fun String.isBeforeDate(date: String): Boolean {
    return runCatching {
        toLocalDate().isBefore(date.toLocalDate())
    }.getOrDefault(false)
}

fun String.equalIgnoreCase(other: String): Boolean {
    return this.equals(other, ignoreCase = true)
}

fun String.checkInputType(text: String): Boolean{
    val pattern = Regex("^\\d+\$")

    return if (!text.equalIgnoreCase("ONLY_NUMBER")){
        true
    }else{
        pattern.matches(
            this
        )
    }
}

fun String.toPowerValue(power: Int): AnnotatedString {
    return buildAnnotatedString {
        append(this@toPowerValue)
        append(" ")

        append("m")

        withStyle(
            style = SpanStyle(
                fontSize = 10.sp,
                baselineShift = BaselineShift.Superscript
            )
        ) {
            append(power.toString())
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
fun String.decodeJwtPayload(): String? {

    return try {

        val split = split(".")

        if (split.size < 2) return null

        getJson(split[1])

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun getJson(strEncoded: String): String {

    val normalized = strEncoded
        .replace('-', '+')
        .replace('_', '/')
        .let {
            when (it.length % 4) {
                2 -> "$it=="
                3 -> "$it="
                else -> it
            }
        }

    val decodedBytes = kotlin.io.encoding.Base64.decode(normalized)

    return decodedBytes.decodeToString()
}


@OptIn(ExperimentalTime::class)
private fun String.parseToLocalDateTime(): kotlinx.datetime.LocalDateTime {
    return try {
        // Handles:
        // 2026-05-11T14:47:58.224597+06:00
        Instant.parse(this)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    } catch (e: Exception) {
        // Handles:
        // 2026-05-13T18:03:37.663418
        kotlinx.datetime.LocalDateTime.parse(this)
    }
}

fun String.toDateOnly(): String {
    return try {
        parseToLocalDateTime().date.toString()
    } catch (e: Exception) {
        this
    }
}

fun String.toTimeOnly(): String {
    return try {
        val localDateTime = parseToLocalDateTime()

        val hour24 = localDateTime.hour
        val minute = localDateTime.minute.toString().padStart(2, '0')

        val amPm = if (hour24 >= 12) "PM" else "AM"

        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }

        "${hour12.toString().padStart(2, '0')}:$minute $amPm"

    } catch (e: Exception) {
        this
    }
}

fun String.toDateTime(): String {
    return try {
        val localDateTime = parseToLocalDateTime()

        val hour24 = localDateTime.hour
        val minute = localDateTime.minute.toString().padStart(2, '0')

        val amPm = if (hour24 >= 12) "PM" else "AM"

        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }

        "${localDateTime.date} ${
            hour12.toString().padStart(2, '0')
        }:$minute $amPm"

    } catch (e: Exception) {
        this
    }
}

