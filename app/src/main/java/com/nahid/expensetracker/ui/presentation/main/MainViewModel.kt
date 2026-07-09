package com.nahid.expensetracker.ui.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nahid.expensetracker.data.local.AppPreference
import com.nahid.expensetracker.domain.model.User
import com.nahid.expensetracker.domain.repository.AuthRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val appPreference: AppPreference,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(MainUiState())
    var uiState: StateFlow<MainUiState> = combine(
        mutableUiState, appPreference.readUserGmail()
    ) { state, gmail ->
        state.copy(gmail = gmail)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), MainUiState())

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
            updateUiState(uiState.value.copy(isLoading = true))
            appPreference.clearAll()
            authRepository.logout()
            updateUiState(uiState.value.copy(isLoading = false, loggedOut = true))
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
    val firebaseUser: FirebaseUser? = null,
    val message: String? = null,
    val gmail: String? = null,
    val showLogoutDialog: Boolean = false,
    val title: String = "Home",
    val loggedOut: Boolean = false,
    val uiConfig: MainUIConfig = MainUIConfig(),
)