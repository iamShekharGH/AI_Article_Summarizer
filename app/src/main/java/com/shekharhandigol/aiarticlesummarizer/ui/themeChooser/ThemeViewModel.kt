package com.shekharhandigol.aiarticlesummarizer.ui.themeChooser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shekharhandigol.aiarticlesummarizer.data.datastore.DatastoreDao
import com.shekharhandigol.aiarticlesummarizer.util.AppThemeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val datastoreDao: DatastoreDao
) : ViewModel() {

    val currentAppTheme: StateFlow<AppThemeOption> = datastoreDao.selectedAppTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppThemeOption.SYSTEM_DEFAULT
        )

    fun updateTheme(themeOption: AppThemeOption) {
        viewModelScope.launch {
            datastoreDao.setSelectedAppTheme(themeOption)
        }
    }
}