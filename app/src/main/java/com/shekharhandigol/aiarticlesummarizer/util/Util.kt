package com.shekharhandigol.aiarticlesummarizer.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.clear
import kotlin.text.none


fun getDayOfMonthSuffix(millis: Long): String {
    val day = SimpleDateFormat("d", Locale.getDefault()).format(Date(millis)).toInt()
    return when (day) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
}

fun simpleMarkdownToAnnotatedString(text: String): AnnotatedString {
    return buildAnnotatedString {

        var currentIndex = 0

        val combinedRegex =
            """(\*\*(.*?)\*\*)|(\*(.*?)\*)|(\_ (.*?)\_)|(\[([^\]]+)\]\(([^)]+)\))""".toRegex()

        combinedRegex.findAll(text).forEach { matchResult ->
            val matchStart = matchResult.range.first
            val matchEnd = matchResult.range.last + 1

            // Append text before the match
            if (matchStart > currentIndex) {
                append(text.substring(currentIndex, matchStart))
            }

            when {
                // Bold: **text**
                matchResult.groups[1] != null -> {
                    val content = matchResult.groups[2]!!.value
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(content)
                    pop()
                }
                // Italic: *text*
                matchResult.groups[3] != null -> {
                    val content = matchResult.groups[4]!!.value
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(content)
                    pop()
                }
                // Italic: _text_
                matchResult.groups[5] != null -> {
                    val content = matchResult.groups[6]!!.value
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(content)
                    pop()
                }
                // Link: [linkText](url)
                matchResult.groups[7] != null -> {
                    val linkText = matchResult.groups[8]!!.value
                    val url = matchResult.groups[9]!!.value
                    pushStyle(
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                    // Add an annotation for the URL, which can be retrieved for click handling
                    addStringAnnotation(
                        tag = "URL",
                        annotation = url,
                        start = length, // current length of annotated string
                        end = length + linkText.length
                    )
                    append(linkText)
                    pop()
                }
            }
            currentIndex = matchEnd
        }

        // Append any remaining text after the last match
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}

enum class SummaryLength(val value: String) {
    SHORT("Short"), MEDIUM("Medium"), LONG("Long"), FORMATTED("Formatted")
}

enum class GeminiModelName(val value: String) {
    GEMINI_PRO("gemini-pro"),
    GEMINI_PRO_VISION("gemini-pro-vision"),
    GEMINI_1_5_FLASH("gemini-1.5-flash"),
    GEMINI_1_5_PRO("gemini-1.5-pro")

}