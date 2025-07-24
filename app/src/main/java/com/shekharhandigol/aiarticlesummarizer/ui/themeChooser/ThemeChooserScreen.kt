package com.shekharhandigol.aiarticlesummarizer.ui.themeChooser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import com.shekharhandigol.aiarticlesummarizer.util.toDisplayString

@Composable
fun ThemeChooserScreen() {
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val currentTheme by themeViewModel.currentAppTheme.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Choose App Theme")
        AppThemeOption.entries.forEach { themeOption ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (themeOption == currentTheme),
                        onClick = { themeViewModel.updateTheme(themeOption) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (themeOption == currentTheme),
                    onClick = null // Handled by Row's selectable modifier
                )
                Text(
                    text = themeOption.toDisplayString(),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}