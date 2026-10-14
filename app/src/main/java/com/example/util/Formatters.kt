package com.example.util

import java.text.DecimalFormat
import java.util.Locale

object Formatters {
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val integerFormat = DecimalFormat("#,##0")

    fun formatMoney(amount: Double): String {
        return when {
            amount >= 1_000_000_000_000.0 -> String.format(Locale.US, "$%.2fT", amount / 1_000_000_000_000.0)
            amount >= 1_000_000_000.0 -> String.format(Locale.US, "$%.2fB", amount / 1_000_000_000.0)
            amount >= 1_000_000.0 -> String.format(Locale.US, "$%.2fM", amount / 1_000_000.0)
            amount >= 10_000.0 -> String.format(Locale.US, "$%.1fK", amount / 1_000.0)
            amount >= 1_000.0 -> String.format(Locale.US, "$%.2fK", amount / 1_000.0)
            amount >= 100.0 -> String.format(Locale.US, "$%.1f", amount)
            amount >= 1.0 -> String.format(Locale.US, "$%.2f", amount)
            amount > 0.0 -> String.format(Locale.US, "$%.2f", amount)
            else -> "$0.00"
        }
    }

    fun formatIncomeRate(rate: Double): String {
        return "${formatMoney(rate)}/s"
    }

    fun formatNumber(number: Long): String {
        return integerFormat.format(number)
    }

    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
    }
}
