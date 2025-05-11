package com.shekharhandigol.aiarticlesummarizer.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onDarkModeChange: (Boolean) -> Unit = {},
    onSummaryLengthChange: (String) -> Unit = {},
    currentDarkMode: Boolean = true,
    currentSummaryLength: String = "To be determined"
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode", style = TextStyle(fontSize = 18.sp))
                Switch(
                    checked = currentDarkMode,
                    onCheckedChange = { onDarkModeChange(it) }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Summary Length", style = TextStyle(fontSize = 18.sp))
            val summaryLengthOptions = listOf("Short", "Medium", "Long")
            var expanded by remember { mutableStateOf(false) }
            Box {
                Text(
                    text = currentSummaryLength,
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(8.dp),
                    style = TextStyle(fontSize = 16.sp),

                    )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    summaryLengthOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                onSummaryLengthChange(option)
                                expanded = false
                            })
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(
        onDarkModeChange = {},
        onSummaryLengthChange = {},
        currentDarkMode = true,
        currentSummaryLength = "Medium"
    )
}