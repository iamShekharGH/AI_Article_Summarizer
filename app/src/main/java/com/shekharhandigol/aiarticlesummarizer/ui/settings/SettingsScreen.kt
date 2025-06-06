package com.shekharhandigol.aiarticlesummarizer.ui.settings

import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shekharhandigol.aiarticlesummarizer.util.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.util.SummaryLength

@Composable
fun MainSettingsScreen() {

    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val darkModeToggleState = viewModel.darkMode.collectAsStateWithLifecycle()
    val promptSettings = viewModel.promptSettings.collectAsStateWithLifecycle()
    val geminiModelName = viewModel.geminiModel.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.getPromptSettings()
        viewModel.getDarkModeValue()
        viewModel.getGeminiModelName()
    }

    LaunchedEffect(darkModeToggleState.value) {
        if (darkModeToggleState.value) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    SettingsScreen(
        darkModeToggleState = darkModeToggleState,
        promptSettings = promptSettings,
        geminiModelName = geminiModelName,
        setDarkMode = { state ->
            viewModel.setDarkModeValue(
                state
            )
        },
        setSummaryLength = { summaryLength ->
            viewModel.saveSummariesPromptSettings(summaryLength)
        },
        onGeminiModelChange = { geminiModel ->
            viewModel.setGeminiModel(geminiModel)
        }
    )

}

@Composable
fun SettingsScreen(
    darkModeToggleState: State<Boolean>,
    promptSettings: State<SummaryLength>,
    geminiModelName: State<GeminiModelName>,
    setDarkMode: (Boolean) -> Unit,
    setSummaryLength: (SummaryLength) -> Unit,
    onGeminiModelChange: (GeminiModelName) -> Unit
) {
    var summaryMenuExpanded by remember { mutableStateOf(false) }
    var geminiMenuExpanded by remember { mutableStateOf(false) }

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
                Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = darkModeToggleState.value,
                    onCheckedChange = {
                        setDarkMode(it)
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Summary Length", style = MaterialTheme.typography.titleMedium)

            Box {
                Text(
                    text = promptSettings.value.value,
                    modifier = Modifier
                        .clickable { summaryMenuExpanded = true }
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,

                    )
                DropdownMenu(
                    expanded = summaryMenuExpanded,
                    onDismissRequest = { summaryMenuExpanded = false }
                ) {
                    SummaryLength.entries.forEach { summaryLengthOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = summaryLengthOption.value,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                setSummaryLength(summaryLengthOption)
                                summaryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Gemini Model", style = MaterialTheme.typography.titleMedium)
            Box {
                Text(
                    text = geminiModelName.value.value,
                    modifier = Modifier
                        .clickable { geminiMenuExpanded = true }
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,

                    )
                DropdownMenu(
                    expanded = geminiMenuExpanded,
                    onDismissRequest = { geminiMenuExpanded = false }
                ) {
                    GeminiModelName.entries.forEach { geminiModelOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = geminiModelOption.value,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {

                                onGeminiModelChange(geminiModelOption)
                                geminiMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedButton(onClick = {
                throw RuntimeException("Test Crash") // Force a crash
            }) {
                Text("Crash", style = MaterialTheme.typography.labelLarge)
            }

        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    val darkMode = remember { mutableStateOf(true) }
    val promptSettings = remember { mutableStateOf(SummaryLength.MEDIUM) }
    val geminiModel = remember { mutableStateOf(GeminiModelName.GEMINI_1_5_FLASH) }
    SettingsScreen(
        darkModeToggleState = darkMode,
        promptSettings = promptSettings,
        geminiModelName = geminiModel,
        setDarkMode = {},
        setSummaryLength = {},
        onGeminiModelChange = {}

    )
}