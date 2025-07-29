package com.shekharhandigol.aiarticlesummarizer.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shekharhandigol.aiarticlesummarizer.R
import com.shekharhandigol.aiarticlesummarizer.core.GeminiModelName
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import com.shekharhandigol.aiarticlesummarizer.util.toDisplayString
import kotlinx.coroutines.launch

@Composable
fun MainSettingsScreen(
    openThemesChooser: () -> Unit,
    openSigninWithGoogle: () -> Unit
) {

    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val promptSettings = viewModel.promptSettings.collectAsStateWithLifecycle()
    val geminiModelName = viewModel.geminiModel.collectAsStateWithLifecycle()
    val themeName = viewModel.themeName.collectAsStateWithLifecycle()

    val importResult = viewModel.importStatus.collectAsStateWithLifecycle()
    val exportResult = viewModel.exportStatus.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(importResult.value, exportResult.value) {
        val importStatus = importResult.value
        val exportStatus = exportResult.value

        val importMessage = if (importStatus.isNotBlank()) "Import: $importStatus" else ""
        val exportMessage = if (exportStatus.isNotBlank()) "Export: $exportStatus" else ""

        val message = when {
            importMessage.isNotBlank() && exportMessage.isNotBlank() -> "$importMessage\n$exportMessage"
            importMessage.isNotBlank() -> importMessage
            exportMessage.isNotBlank() -> exportMessage
            else -> ""
        }
        if (message.isNotBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.setStatus("")
            }
        }
    }


    // Launcher for picking a file to save (export)
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.exportClicked(it)
        } ?: run {
            viewModel.setStatus("Export cancelled.")
        }
    }

    // Launcher for picking a file to open (import)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.importClicked(it)
        } ?: run {
            viewModel.setStatus("Import cancelled.")
        }
    }


    LaunchedEffect(Unit) {
        viewModel.getPromptSettings()
        viewModel.getGeminiModelName()
        viewModel.getThemeName()
    }

    SettingsScreen(
        promptSettings = promptSettings,
        geminiModelName = geminiModelName,
        themeName = themeName,
        setSummaryLength = { summaryLength ->
            viewModel.saveSummariesPromptSettings(summaryLength)
        },
        onGeminiModelChange = { geminiModel ->
            viewModel.setGeminiModel(geminiModel)
        },
        openThemesChooser = openThemesChooser,
        openLoginPage = openSigninWithGoogle,
        importClicked = { importLauncher.launch(arrayOf("application/json")) },
        exportClicked = {
            exportLauncher.launch("summaries_backup.json")
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun SettingsScreen(
    promptSettings: State<SummaryType>,
    geminiModelName: State<GeminiModelName>,
    themeName: State<AppThemeOption>,
    setSummaryLength: (SummaryType) -> Unit,
    onGeminiModelChange: (GeminiModelName) -> Unit,
    openThemesChooser: () -> Unit,
    openLoginPage: () -> Unit,
    importClicked: () -> Unit,
    exportClicked: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var summaryMenuExpanded by remember { mutableStateOf(false) }
    var geminiMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Summary Length", style = MaterialTheme.typography.titleMedium)

            Box {
                Text(
                    text = promptSettings.value.displayName,
                    modifier = Modifier
                        .clickable { summaryMenuExpanded = true }
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,

                    )
                DropdownMenu(
                    expanded = summaryMenuExpanded,
                    onDismissRequest = { summaryMenuExpanded = false }
                ) {
                    SummaryType.entries.forEach { summaryLengthOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = summaryLengthOption.displayName,
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Choose Theme", style = MaterialTheme.typography.titleMedium)
            Text(
                text = themeName.value.toDisplayString(),
                modifier = Modifier
                    .clickable { openThemesChooser() }
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,

                )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        exportClicked()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Text(text = "Export")
                }
                OutlinedButton(
                    onClick = {
                        importClicked()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text(text = "Import")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


            OutlinedButton(
                onClick = {
                    openLoginPage()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_fancy_image),
                    contentDescription = "Sign in with Google",
                    modifier = Modifier.size(24.dp)
                )
                Text("Sign in with Google", modifier = Modifier.padding(start = 8.dp))
            }

        }

        SnackbarHost(
            hostState = snackbarHostState
        )
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    remember { mutableStateOf(true) }
    val promptSettings = remember { mutableStateOf(SummaryType.MEDIUM_SUMMARY) }
    val geminiModel = remember { mutableStateOf(GeminiModelName.GEMINI_1_5_FLASH) }
    val themeName = remember { mutableStateOf(AppThemeOption.LIGHT_HIGH_CONTRAST) }
    SettingsScreen(
        promptSettings = promptSettings,
        geminiModelName = geminiModel,
        themeName = themeName,
        setSummaryLength = {},
        onGeminiModelChange = {},
        openThemesChooser = {},
        openLoginPage = {},
        importClicked = {},
        exportClicked = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}