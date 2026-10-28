package com.example.syncmate.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.roundToLong

object CurrencyFormatter {
    private val inLocale = Locale.Builder().setLanguage("en").setRegion("IN").build()
    private val currencyInstance: NumberFormat = try {
        NumberFormat.getCurrencyInstance(inLocale).apply {
            currency = Currency.getInstance("INR")
        }
    } catch (_: Exception) {
        NumberFormat.getCurrencyInstance().apply {
            try {
                currency = Currency.getInstance("INR")
            } catch (_: Exception) {}
        }
    }

    /**
     * Formats an amount with Indian number grouping (lakhs/crores) and INR symbol '₹'.
     * Example: 15000 -> "₹15,000", 125000 -> "₹1,25,000", 8500 -> "₹8,500"
     */
    fun formatInr(amount: Number, includeDecimals: Boolean = false): String {
        val doubleVal = amount.toDouble()
        if (doubleVal.isNaN() || doubleVal.isInfinite()) return "₹0"

        val isNegative = doubleVal < 0
        val positiveVal = if (isNegative) -doubleVal else doubleVal

        val longPart = positiveVal.toLong()
        val decimalPart = ((positiveVal - longPart) * 100).roundToLong()

        val grouped = formatIndianGrouping(longPart)
        val formatted = if (includeDecimals && decimalPart > 0) {
            "₹$grouped.${decimalPart.toString().padStart(2, '0')}"
        } else {
            "₹$grouped"
        }

        return if (isNegative) "-$formatted" else formatted
    }

    /**
     * Formats monthly rent as "₹15,000/mo"
     */
    fun formatMonthlyRent(amount: Number): String {
        return "${formatInr(amount)}/mo"
    }

    /**
     * Formats monthly rent as "₹15,000/month"
     */
    fun formatMonthlyRentFull(amount: Number): String {
        return "${formatInr(amount)}/month"
    }

    /**
     * Formats a range in INR, e.g. "₹12,000 - ₹16,500"
     */
    fun formatInrRange(low: Number, high: Number): String {
        return "${formatInr(low)} - ${formatInr(high)}"
    }

    /**
     * Formats just the number using Indian numbering system (e.g., 15,000 or 1,25,000)
     */
    fun formatIndianGrouping(number: Long): String {
        val s = number.toString()
        if (s.length <= 3) return s

        val lastThree = s.substring(s.length - 3)
        val rest = s.substring(0, s.length - 3)

        val sb = StringBuilder()
        for (i in rest.indices) {
            sb.append(rest[i])
            val remaining = rest.length - 1 - i
            if (remaining > 0 && remaining % 2 == 0) {
                sb.append(',')
            }
        }
        sb.append(',')
        sb.append(lastThree)
        return sb.toString()
    }
}
