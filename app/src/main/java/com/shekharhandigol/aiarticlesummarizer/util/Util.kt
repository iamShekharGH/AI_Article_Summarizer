package com.shekharhandigol.aiarticlesummarizer.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun getDayOfMonthSuffix(millis: Long): String {
    val day = SimpleDateFormat("d", Locale.getDefault()).format(Date(millis)).toInt()
    return when (day) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
}

enum class SummaryLength(val value: String) {
    SHORT("Short"), MEDIUM("Medium"), LONG("Long")
}

enum class GeminiModelName(val value: String) {
    GEMINI_PRO("gemini-pro"),
    GEMINI_PRO_VISION("gemini-pro-vision"),
    GEMINI_1_5_FLASH("gemini-1.5-flash"),
    GEMINI_1_5_PRO("gemini-1.5-pro")

}