package com.shekharhandigol.aiarticlesummarizer.data

const val SUMMARIZE_ARTICLE_PROMPT_SHORT =
    "Summarize the following article concisely in less than 100 words:"
const val SUMMARIZE_ARTICLE_PROMPT_MEDIUM =
    "Summarize the following article in less than 250 words:"
const val SUMMARIZE_ARTICLE_PROMPT_LARGE =
    "Summarize the following article comprehensively in less than 300 words:"
const val FULL_DETAILS_WITH_BULLET_POINTS =
    "Please summarize the following article in simple terms, using bullet points starting with \"-» \", focusing on the main ideas and key takeaways. Ensure the summary is highly readable, easy for someone without prior knowledge to understand, and well-formatted. Avoid complex details or jargon."
// Prompts designed to aid deeper comprehension and specific insights

// User-facing name implies: "Get the main point of the article."
const val PROMPT_GET_ARTICLE_MAIN_PURPOSE =
    "Based on the article, what is the author's main purpose or central message? Summarize it in one to two sentences."

// User-facing name implies: "List the important questions answered in the article."
const val PROMPT_LIST_ARTICLE_KEY_QUESTIONS =
    "List 3-5 key questions that this article answers or attempts to address, using '-» ' for each bullet point."

// User-facing name implies: "Break down the core arguments or claims."
const val PROMPT_BREAKDOWN_ARTICLE_ARGUMENTS =
    "Break down the article's main arguments or claims into bullet points, using '-» ' for each point. For each argument, briefly state the core idea."

// User-facing name implies: "Identify the most important things to remember."
const val PROMPT_IDENTIFY_ARTICLE_KEY_TAKEAWAYS =
    "What are the most important implications or key takeaways from this article that someone should remember? Explain in bullet points, using '-» ' for each point."

// User-facing name implies: "Simplify a difficult concept from the article."
const val PROMPT_SIMPLIFY_ARTICLE_COMPLEX_IDEA =
    "Explain the most complex concept or idea from this article in simple terms, as if explaining it to a beginner. Avoid bullet points for this explanation."