package com.shekharhandigol.aiarticlesummarizer.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Loading)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()


    fun loginWithEmailPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { LoginUIState.Error }
            return
        }

        viewModelScope.launch {


        }
    }
}

sealed interface LoginUIState {
    data object Success : LoginUIState
    data object Error : LoginUIState
    data object Failed : LoginUIState
    data object Loading : LoginUIState
}