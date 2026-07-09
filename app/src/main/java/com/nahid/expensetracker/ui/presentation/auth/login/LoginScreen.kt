package com.nahid.expensetracker.ui.presentation.auth.login

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.R
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.core.Logger
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.theme.Black
import com.nahid.expensetracker.ui.theme.PurpleGrey80
import com.nahid.expensetracker.ui.theme.White
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

private const val TAG = "LoginScreen"

@OptIn(KoinExperimentalAPI::class)
@Composable
fun LoginScreen(
    toHome: () -> Unit,
    onChangeUiConfiguration: (MainUIConfig) -> Unit = {},
    onShowMessage: (String) -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        onChangeUiConfiguration(state.config)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    val message = event.message
                    Logger.log(TAG, message.toString())
                    onShowMessage(message.second)
                }

                UiEvent.NavigateBack -> {}
                UiEvent.NavigateToHome -> toHome()
            }
        }
    }


    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = AppSpacing.Layout.sectionGap, horizontal = AppSpacing.Layout.screenPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(AppSpacing.Size.xxl))
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .background(PurpleGrey80),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.login_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                    )
                }
                Spacer(Modifier.height(AppSpacing.Size.sm))
                Text(
                    "Expense Tracker",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(Modifier.height(AppSpacing.Size.sm))

                Text(
                    "Save money! The more your money works for you, the less you have to work for money.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                )
            }

            if (!state.isLoading) {
                ElevatedButton(
                    onClick = {
                        viewModel.googleLogin(context)
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(.80f)
                        .padding(bottom = AppSpacing.Layout.sectionGap),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.google_icon),
                            tint = Color.Unspecified,
                            contentDescription = null,
                            modifier = Modifier.padding(end = AppSpacing.Size.sm)
                        )
                        Text("Sign in with Google")
                    }
                }
            }
        }

        if (state.isLoading) {
            AnimatedProgressDialog()
        }
    }
}
