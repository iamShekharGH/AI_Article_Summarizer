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

fun simpleMarkdownToAnnotatedString(text: String): AnnotatedString {
    Log.d("simpleMarkdownToAnnotatedString", "Before processing: $text")
    return buildAnnotatedString {
        Log.d("simpleMarkdownToAnnotatedString", "Starting buildAnnotatedString")
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
                "simpleMarkdownToAnnotatedString",
                "Appending remaining text: ${text.substring(currentIndex)}"
            )
            append(text.substring(currentIndex))
        }
        Log.d("simpleMarkdownToAnnotatedString", "Finished buildAnnotatedString")
    }.also {
        Log.d("simpleMarkdownToAnnotatedString", "After processing, AnnotatedString: $it")
    }
}


enum class SummaryType(val displayName: String, val prompt: String) {
    // Length-based summaries
    SHORT_SUMMARY(
        "Short Summary (Under 100 words)",
        "Summarize the following article concisely in less than 100 words:"
    ),
    MEDIUM_SUMMARY(
        "Medium Summary (Under 250 words)",
        "Summarize the following article in less than 250 words:"
    ),
    LARGE_SUMMARY( // Renamed from LONG for consistency
        "Large Summary (Under 300 words)",
        "Summarize the following article comprehensively in less than 300 words:"
    ),

    // Formatted summary with specific bullet points
    BULLETED_SUMMARY( // More descriptive name than "Formatted"
        "Bulleted Summary",
        "Please summarize the following article in simple terms, using bullet points starting with \"-» \", focusing on the main ideas and key takeaways. Ensure the summary is highly readable, easy for someone without prior knowledge to understand, and well-formatted. Avoid complex details or jargon."
    ),

    // Comprehension-focused prompts
    GET_MAIN_PURPOSE(
        "Article's Main Purpose",
        "Based on the article, what is the author's main purpose or central message? Summarize it in one to two sentences."
    ),
    LIST_KEY_QUESTIONS(
        "Key Questions Answered",
        "List 3-5 key questions that this article answers or attempts to address, using '-» ' for each bullet point."
    ),
    BREAKDOWN_ARGUMENTS(
        "Break Down Main Arguments",
        "Break down the article's main arguments or claims into bullet points, using '-» ' for each point. For each argument, briefly state the core idea."
    ),
    IDENTIFY_KEY_TAKEAWAYS(
        "Key Takeaways",
        "What are the most important implications or key takeaways from this article that someone should remember? Explain in bullet points, using '-» ' for each point."
    ),
    SIMPLIFY_COMPLEX_IDEA(
        "Simplify a Complex Idea",
        "Explain the most complex concept or idea from this article in simple terms, as if explaining it to a beginner. Avoid bullet points for this explanation."
    )
}

enum class GeminiModelName(val value: String) {
    GEMINI_PRO("gemini-pro"),
    GEMINI_PRO_VISION("gemini-pro-vision"),
    GEMINI_1_5_FLASH("gemini-1.5-flash"),
    GEMINI_1_5_PRO("gemini-1.5-pro"),
    GEMINI_2_0_FLASH("gemini-2.0-flash")
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