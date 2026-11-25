package com.nahid.expensetracker.domain.model

import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatterBuilder
import java.time.format.ResolverStyle
import java.util.Locale

fun String.asTransactionDate(): LocalDate? {
    runCatching { LocalDate.parse(this) }.getOrNull()?.let { return it }
    return sequenceOf(Locale.getDefault(), Locale.ENGLISH, Locale.forLanguageTag("bn-BD"))
        .distinct()
        .firstNotNullOfOrNull { locale ->
            val format = DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-uuuu")
                .toFormatter(locale).withResolverStyle(ResolverStyle.STRICT)
            runCatching { LocalDate.parse(this, format) }.getOrNull()
        }
}

val recentTransactionOrder = compareByDescending<Expense> { it.date.asTransactionDate() }
    .thenByDescending { it.id }

fun Expense.isInMonth(month: YearMonth): Boolean =
    date.asTransactionDate()?.let(YearMonth::from) == month

fun Long.asTaka(): String = "৳" + NumberFormat.getIntegerInstance().format(this)

fun String.validTransactionAmount(): Int? = trim().toIntOrNull()?.takeIf { it > 0 }

fun transactionDateFromPicker(utcMillis: Long): LocalDate =
    Instant.ofEpochMilli(utcMillis).atOffset(ZoneOffset.UTC).toLocalDate()

fun LocalDate.toPickerMillis(): Long = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
