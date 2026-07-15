package com.nahid.expensetracker.ui.presentation.transections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransectionsViewModel : ViewModel() {
    private val mutableUiState = MutableStateFlow(TransectionsUiState())
    private val mutableUiEvent = MutableSharedFlow<TransectionsUiEvent>()

    val uiState: StateFlow<TransectionsUiState> = mutableUiState.asStateFlow()
    val uiEvent: SharedFlow<TransectionsUiEvent> = mutableUiEvent.asSharedFlow()

    fun updateUiState(uiState: TransectionsUiState) {
        mutableUiState.value = uiState
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(TransectionsUiEvent.ShowMessage(Pair(isSuccess, message)))
        }
    }
}

sealed interface TransectionsUiEvent {
    data class ShowMessage(val message: Pair<Boolean, String>) : TransectionsUiEvent
}

data class TransectionsUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Transections",
        showTopBar = true,
        showNavigation = true,
        showSubTitle = false
    )
)