package com.shekharhandigol.aiarticlesummarizer.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DATABASE_NAME = "ARTICLE_TABLE"

fun getDayOfMonthSuffix(millis: Long): String {
    val day = SimpleDateFormat("d", Locale.getDefault()).format(Date(millis)).toInt()
    return when (day) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
}