package com.shekharhandigol.aiarticlesummarizer.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun FeedbackScreen(
    onFeedbackSubmit: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var feedback by remember { mutableStateOf("") }
            TextField(
                value = feedback,
                onValueChange = { feedback = it },
                label = { Text("Enter your feedback") },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f) // Adjust height as needed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onFeedbackSubmit(feedback) },
                modifier = Modifier.fillMaxWidth(),
                enabled = feedback.isNotBlank()
            ) {
                Text("Submit Feedback")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewFeedbackScreen() {
    FeedbackScreen(
        onFeedbackSubmit = {}
    )
}