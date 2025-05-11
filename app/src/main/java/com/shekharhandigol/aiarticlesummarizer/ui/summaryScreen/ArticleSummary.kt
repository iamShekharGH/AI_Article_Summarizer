package com.shekharhandigol.aiarticlesummarizer.ui.summaryScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryScreen(
    title: String,
    summary: String
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title, style = TextStyle(fontSize = 20.sp),
                fontStyle = FontStyle.Italic
            )
            Text(
                text = summary,
                style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SummaryScreenPreview() {
    SummaryScreen(
        title = "Kotlin’s Builder Functions: A Better Way to Create Lists, Maps, Strings & Sets",
        summary = "Kotlin's builder functions simplify the creation of common data structures. buildList {} creates immutable lists from mutable operations within a lambda, with primitive-optimized versions like buildIntList {}. buildString {} offers a concise way to build strings using a StringBuilder in a lambda. buildSet {} constructs immutable sets, with type-specific options like buildIntSet {} (note: order is not guaranteed). buildMap {} facilitates immutable map creation, including specialized versions like buildIntIntMap {} for primitives. These standard library features reduce boilerplate and enhance code readability for object construction. Other libraries also provide similar builder utilities.",

        )
}