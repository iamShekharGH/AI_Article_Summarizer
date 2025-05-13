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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                Text("Dark Mode", style = TextStyle(fontSize = 18.sp))
                Switch(
                    checked = darkModeToggleState.value,
                    onCheckedChange = {
                        setDarkMode(it)
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Summary Length", style = TextStyle(fontSize = 18.sp))

            Box {
                Text(
                    text = promptSettings.value.value,
                    modifier = Modifier
                        .clickable { summaryMenuExpanded = true }
                        .padding(8.dp),
                    style = TextStyle(fontSize = 16.sp),

                    )
                DropdownMenu(
                    expanded = summaryMenuExpanded,
                    onDismissRequest = { summaryMenuExpanded = false }
                ) {
                    SummaryLength.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option.value) },
                            onClick = {
                                setSummaryLength(option)
                                summaryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Gemini Model", style = TextStyle(fontSize = 18.sp))
            Box {
                Text(
                    text = geminiModelName.value.value,
                    modifier = Modifier
                        .clickable { geminiMenuExpanded = true }
                        .padding(8.dp),
                    style = TextStyle(fontSize = 16.sp),

                    )
                DropdownMenu(
                    expanded = geminiMenuExpanded,
                    onDismissRequest = { geminiMenuExpanded = false }
                ) {
                    GeminiModelName.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option.value) },
                            onClick = {

                                onGeminiModelChange(option)
                                geminiMenuExpanded = false
                            }
                        )
                    }
                }
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