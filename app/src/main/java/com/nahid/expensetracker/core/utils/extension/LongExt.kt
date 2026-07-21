package com.nahid.expensetracker.core.utils.extension

import android.annotation.SuppressLint
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


import java.util.Locale

fun String.toDateMillis(): Long {
    return try {
        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        sdf.parse(this)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}


@OptIn(ExperimentalTime::class)
private fun Long.toLocalDate() =
    Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())


fun Long.toDate(): String {
    return toLocalDate().day.toString()
}

fun Long.toMonth(): Int {
    return toLocalDate().month.number.toString().padStart(2, '0').toInt()
}

fun Long.toYear(): Int {
    return toLocalDate().year
}

@OptIn(ExperimentalTime::class)
fun Long.toFormat(pattern: String): String {
    val dateTime = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val shortMonths = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    return pattern
        .replace("dd", dateTime.day.toString().padStart(2, '0'))
        .replace("MMM", shortMonths[dateTime.month.number - 1])
        .replace("MM", dateTime.month.number.toString().padStart(2, '0'))
        .replace("yyyy", dateTime.year.toString())
        .replace("HH", dateTime.hour.toString().padStart(2, '0'))
        .replace("mm", dateTime.minute.toString().padStart(2, '0'))
        .replace("ss", dateTime.second.toString().padStart(2, '0'))
}


fun Long.toOnlyDate(): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toOnlyDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}



fun Long.toSimpleTimeOnlyString(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("HH:mm")
    return simpleDateFormat.format(date)
}

@SuppressLint("SimpleDateFormat")
fun Long.longToSimpleDateFormatString(): String {

    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")
    return simpleDateFormat.format(date)
}

@SuppressLint("SimpleDateFormat")
fun Long.longToSimpleDateOnlyString(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    return simpleDateFormat.format(date)
}

fun Long.toDateOnly(): Long {
    return try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        calendar.timeInMillis
    }catch (e: Exception){
        0L
    }

}

fun Long.generateSyncDateTimeFromLocation(serverTimeMilli: Long): Long {
    val serverTime = Calendar.getInstance()
    serverTime.timeInMillis = serverTimeMilli

    val deviceTime = Calendar.getInstance()
    deviceTime.set(Calendar.HOUR_OF_DAY, serverTime.get(Calendar.HOUR_OF_DAY))
    deviceTime.set(Calendar.MINUTE, serverTime.get(Calendar.MINUTE))
    deviceTime.set(Calendar.SECOND, serverTime.get(Calendar.SECOND))
    deviceTime.set(Calendar.MILLISECOND, serverTime.get(Calendar.MILLISECOND))

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.DAY_OF_MONTH, serverTime.get(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.MONTH, serverTime.get(Calendar.MONTH))
    calendar.set(Calendar.YEAR, serverTime.get(Calendar.YEAR))

    return calendar.timeInMillis
}
