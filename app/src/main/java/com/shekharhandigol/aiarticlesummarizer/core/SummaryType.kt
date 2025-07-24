package com.shekharhandigol.aiarticlesummarizer.core

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
    ),
    UNKNOWN(
        "Unknown Summary Type",
        "Unknown summary type. Please select a valid summary option."
    ) // Added for robustness
}


//const val TAG_GENERATION_PROMPT = "Generate 3-5 relevant and generic tags for the following article, with each tag being one or two words max, similar to how you would tag an article about a new car model (e.g., 'car', 'automotive', 'v8 engine', 'bmw'). The article content is:"
const val TAG_GENERATION_PROMPT = """
Generate 3-5 relevant and generic tags for the following article.
Each tag must be one or two words maximum.

**Output Rules:**
- If you can generate relevant tags, your entire response must be ONLY a comma-separated list (e.g., car,automotive,v8 engine,bmw).
- If you cannot generate relevant tags that fit the criteria, your entire response must be completely empty.
- Do not add any explanation, preamble, or apologies.

The article content is:
"""