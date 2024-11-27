package com.grupp.assessment.productexplorer.ui.utils

import java.text.NumberFormat

private val CURRENCY_REGEX = "\\p{Sc}?\\d+(?:,\\d{3})*\\.?\\d*".toRegex()
private val NO_CURRENCY_REGEX = "\\d+(?:,\\d{3})*\\.?\\d*".toRegex()

var CURRENCY = "\u20A6"

/**
 * Goes through the string value, and return the index of every detected monetary value.
 * @param includeCurrency if true, the regex used will also match any possible currency value
 * found, if false only valid numbers will be matched
 * @return a list of the start and end index (inclusive) of all detected monetary string
 */
fun String.extractCurrencyIndices(includeCurrency: Boolean = false): List<IntRange> {
    val result = (if(includeCurrency) CURRENCY_REGEX else NO_CURRENCY_REGEX).findAll(this)

    return result.map { it.range }.toList()
}

/**
 * Goes through the string value, and return the amount substring detected.
 * @return a list of the start and end index (inclusive) of all detected monetary string
 */
fun String.extractCurrency(includeCurrency: Boolean = false): List<String> {
    val result = (if(includeCurrency) CURRENCY_REGEX else NO_CURRENCY_REGEX).findAll(this)

    return result.map { it.value }.toList()
}

/**
 * Change the amount value to a string value that can be worked with
 * example: 1,000,000.00 -> 1000000.00
 * @param currency If the string has a currency attached to it. It will be removed too
 */
fun String.amountAsString(currency: String = CURRENCY): String {
    return replace(",".toRegex(), "").replace(currency, "")
}

fun String.asNumberAmount(): Double {
    if (isEmpty()) return -1.0

    val startIndex = if (this[0].isDigit()) 0 else 1

    val balance = substring(startIndex, length)

    return balance.replace(",".toRegex(), "").toDouble()
}

/**
 * Properly format the amount string
 * example: 1000000.00 -> 1,000,000.00
 * 0000001
 */
fun String?.formatAsAmount(currency: String = CURRENCY, minDecimal: Int = 0, maxDecimal: Int = 2): String {
    if (isNullOrBlank()) return ""

    val sanitized = (if(trim().startsWith(currency)) replace(currency, "") else this).trim()

    return try {
        val numberFormatter = NumberFormat.getNumberInstance().apply {
            minimumFractionDigits = minDecimal
            maximumFractionDigits = maxDecimal
        }

        currency + numberFormatter.format(numberFormatter.parse(sanitized)).trim()
    }
    catch(e: Exception) {
        //TODO: This fails if the decimal in the string is more than 2. And it also doesn't work well with maxDecimal
        val unformatted = sanitized.amountAsString()

        val decimalIndex = unformatted.lastIndexOf('.').let { if(it == -1) unformatted.length else it }
        val decimalPart = unformatted.substring(decimalIndex).let { if(it.isEmpty() && minDecimal > 0) ".${"0".repeat(minDecimal)}" else it }
        val integerPart = unformatted.substring(0, decimalIndex).reversed()

        val formattedIntegerPart = integerPart.toCharArray().mapIndexed { index, c ->
            if(index > 0 && index % 3 == 0) ",$c"
            else "$c"
        }.joinToString(separator = "").reversed()

        val formattedDecimalPart = if(decimalPart == ".00") "" else decimalPart

        "$currency$formattedIntegerPart$formattedDecimalPart"
    }
}

private val formatter = NumberFormat.getInstance().apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

fun Number.formatToBalance(currency: String = CURRENCY): String {
    return toString().formatAsAmount(currency)
//    return formatter.format(this@formatToBalance).run {
//        val decimalIndex = lastIndexOf('.')
//        val decimalDigits = substring(IntRange(decimalIndex + 1, length - 1))
//
//        val formattedBalance = if (decimalDigits.toInt() > 0) this
//        else substring(IntRange(0, decimalIndex - 1))
//
//        buildString {
//            append(formattedBalance)
//            insert(if(formattedBalance[0] == '-') 1 else 0, currency)
//        }.trim()
//    }
}
