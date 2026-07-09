package com.nahid.expensetracker.ui.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.data.local.AppPreference
import com.nahid.expensetracker.domain.model.User
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val appPreference: AppPreference) : ViewModel() {
    private val mutableUiState = MutableStateFlow(MainUiState())
    var uiState = mutableUiState.asStateFlow()
    private val mutableUiEvent = MutableSharedFlow<UiEvent>()

    val uiEvent = mutableUiEvent.asSharedFlow()
    fun showMessage(message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(UiEvent.ShowMessage(message))
        }
    }

    fun updateUiState(uiState: MainUiState) {
        this.mutableUiState.update { uiState }
    }
    fun logout() {
        viewModelScope.launch {

        }
    }
}

sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
}

data class MainUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val user: User? = null,
    val message: String? = null,
    val userName: String? = null,
    val showLogoutDialog: Boolean = false,
    val title: String = "Home",
    val loggedOut: Boolean = false,
    val uiConfig: MainUIConfig = MainUIConfig(),
)