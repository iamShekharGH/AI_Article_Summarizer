package com.shekharhandigol.aiarticlesummarizer.util

import android.util.Log
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


fun getDayOfMonthSuffix(millis: Long): String {
    val day = SimpleDateFormat("d", Locale.getDefault()).format(Date(millis)).toInt()
    return when (day) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
}

private const val MARKDOWN_LOG_TAG = "simpleMarkdown"

fun simpleMarkdownToAnnotatedString(text: String): AnnotatedString {
    Log.d(MARKDOWN_LOG_TAG, "Before processing: $text")
    return buildAnnotatedString {
        Log.d(MARKDOWN_LOG_TAG, "Starting buildAnnotatedString")
        var currentIndex = 0

        // Regex to match all supported markdown patterns,
        // prioritizing more specific patterns like bold over generic italic,
        // and handling newlines and list items separately if needed.
        // The list item regex now specifically looks for a newline followed by '* ' or a line start with '* '
        val combinedRegex =
            """(?m)(\*\*(.*?)\*\*)|(\*(.*?)\*)|(_(.*?)_)|(\[([^\]]+)]\(([^)]+)\))|((?:^|\n)\*\s+(.*))""".toRegex()
        // Group 1: **bold** (2: content)
        // Group 3: *italic* (4: content)
        // Group 5: _italic_ (6: content)
        // Group 7: [linkText](url) (8: linkText, 9: url)
        // Group 10: List Item: (?:^|\n)\*\s+(.*) (11: content)

        combinedRegex.findAll(text).forEach { matchResult ->
            val matchStart = matchResult.range.first
            val matchEnd = matchResult.range.last + 1

            // Append text before the current match
            if (matchStart > currentIndex) {
                append(text.substring(currentIndex, matchStart))
            }

            // Determine which markdown pattern matched
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
                    addStringAnnotation(
                        tag = "URL",
                        annotation = url,
                        start = length,
                        end = length + linkText.length
                    )
                    append(linkText)
                    pop()
                }
                // List Item: (?:^|\n)* content
                matchResult.groups[10] != null -> {
                    // This handles the newline character if it was part of the match
                    if (matchResult.value.startsWith("\n")) {
                        append("\n")
                    } else if (matchStart != 0) { // If it's not the very beginning, ensure a newline if it's a new list item
                        append("\n")
                    }

                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold)) // Bullet point style
                    append("• ") // The desired bullet symbol
                    pop()

                    // Extract the content *after* the bullet marker for further processing
                    val rawContent = matchResult.groups[11]!!.value
                    // Process this raw content for nested markdown (bold, italic, link)
                    append(simpleMarkdownToAnnotatedString(rawContent))
                }
            }
            currentIndex = matchEnd
        }

        // Append any remaining text after the last match
        if (currentIndex < text.length) {
            Log.d(
                MARKDOWN_LOG_TAG,
                "Appending remaining text: ${text.substring(currentIndex)}"
            )
            append(text.substring(currentIndex))
        }
        Log.d(MARKDOWN_LOG_TAG, "Finished buildAnnotatedString")
    }.also {
        Log.d(MARKDOWN_LOG_TAG, "After processing, AnnotatedString: $it")
    }
}

enum class AppThemeOption {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK,
    LIGHT_MEDIUM_CONTRAST,
    LIGHT_HIGH_CONTRAST,
    DARK_MEDIUM_CONTRAST,
    DARK_HIGH_CONTRAST
}

fun AppThemeOption.toDisplayString(): String {
    return this.name.replace("_", " ").lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}