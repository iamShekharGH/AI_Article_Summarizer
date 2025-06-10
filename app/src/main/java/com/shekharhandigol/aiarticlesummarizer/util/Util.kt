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
            """(\*\*(.*?)\*\*)|(\*(.*?)\*)|(_ (.*?)_)|(\[([^]]+)]\(([^)]+)\))|(\n\s*-»\s+(.*))""".toRegex()
//            """(\*\*(.*?)\*\*)|(\*(.*?)\*)|(\_ (.*?)\_)|(\[([^\]]+)\]\(([^)]+)\))""".toRegex()
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