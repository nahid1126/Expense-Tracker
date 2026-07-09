package com.nahid.expensetracker.ui.presentation.auth.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.core.utils.exception.getMessage
import com.nahid.expensetracker.data.local.AppPreference
import com.nahid.expensetracker.data.repository.GoogleAuthManager
import com.nahid.expensetracker.domain.repository.AuthRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val appPreference: AppPreference,
) :
    ViewModel() {

    private val mutableUiState = MutableStateFlow(LoginUiState())
    private val mutableUiEvent = MutableSharedFlow<UiEvent>()

    val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()
    val uiEvent: SharedFlow<UiEvent> = mutableUiEvent.asSharedFlow()

    fun updateUiState(uiState: LoginUiState) {
        mutableUiState.value = uiState
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(UiEvent.ShowMessage(Pair(isSuccess, message)))
        }
    }

    fun googleLogin(context: Context) {
        updateUiState(uiState.value.copy(isLoading = true))
        viewModelScope.launch {
            val idToken = GoogleAuthManager(context).signIn()
            if (idToken != null) {
                val result = authRepository.signInWithGoogle(idToken)
                when (result) {
                    is Results.Error -> {
                        updateUiState(uiState.value.copy(isLoading = false))
                        showMessage(false, result.exception.getMessage())
                    }

                    is Results.Success -> {
                        if (result.data != null) {
                            appPreference.saveUserId(result.data.uid)
                            appPreference.saveUserGmail(result.data.email)
                            Log.d(
                                TAG,
                                "googleLogin: ${result.data.email} ${result.data.providerId}, ${result.data.uid}"
                            )
                        }
                        updateUiState(
                            uiState.value.copy(
                                isLoading = false,
                                isLoginSuccess = true,
                                userInfo = result.data,
                            )
                        )
                        mutableUiEvent.emit(UiEvent.NavigateToHome)
                        showMessage(true, "Login Success")
                    }
                }
            } else {
                updateUiState(uiState.value.copy(isLoading = false))
            }
        }
    }
}

sealed interface UiEvent {
    data class ShowMessage(val message: Pair<Boolean, String>) : UiEvent
    object NavigateBack : UiEvent
    object NavigateToHome : UiEvent
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val androidId: String = "1234",
    val appVersion: String = "1.0.0",
    val isAndroid: Boolean = true,
    val message: Pair<Boolean, String>? = null,
    val config: MainUIConfig = MainUIConfig(
        title = "",
        showNavigation = false,
        showTopBar = false
    ),
    val isLoginSuccess: Boolean = false,
    val userInfo: FirebaseUser? = null,
)
