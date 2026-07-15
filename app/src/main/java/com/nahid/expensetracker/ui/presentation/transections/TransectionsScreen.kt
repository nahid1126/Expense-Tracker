package com.nahid.expensetracker.ui.presentation.transections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransectionsScreen(
    viewModel: TransectionsViewModel = koinViewModel(),
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        onChangeConfiguration(
            state.uiConfig
        )
        delay(200)
        //startAnimation = true
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is TransectionsUiEvent.ShowMessage -> {
                    onShowMessage(it.message.second)
                }
            }
        }
    }
}