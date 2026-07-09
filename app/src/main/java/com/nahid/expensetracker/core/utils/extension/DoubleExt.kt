package com.nahid.expensetracker.core.utils.extension

import kotlin.math.round

fun Double.toOneDecimalFormatString(): String {
    return formatDecimal(1)
}

fun Double.toTwoDecimalFormatString(): String {
    return formatDecimal(2)
}

fun Double.toThreeDecimalFormatString(): String {
    return formatDecimal(3)
}

private fun Double.formatDecimal(decimalPlaces: Int): String {
    val factor = 10.0.pow(decimalPlaces)

    val rounded = round(this * factor) / factor

    return rounded.toString().let { value ->
        if (value.contains(".")) {
            val parts = value.split(".")
            val decimalPart = parts.getOrElse(1) { "" }

            buildString {
                append(parts[0])

                if (decimalPlaces > 0) {
                    append(".")
                    append(decimalPart.padEnd(decimalPlaces, '0').take(decimalPlaces))
                }
            }
        } else {
            buildString {
                append(value)

                if (decimalPlaces > 0) {
                    append(".")
                    repeat(decimalPlaces) {
                        append("0")
                    }
                }
            }
        }
    }
}

private fun Double.pow(n: Int): Double {
    var result = 1.0
    repeat(n) {
        result *= this
    }
    return result
}