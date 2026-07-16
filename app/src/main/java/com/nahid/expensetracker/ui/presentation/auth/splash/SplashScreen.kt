package com.nahid.expensetracker.ui.presentation.auth.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nahid.expensetracker.R
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.data.local.AppPreference
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    toHome: () -> Unit,
    toLogin: () -> Unit,
    onChangeUiConfiguration: (MainUIConfig) -> Unit = {},
) {

    LaunchedEffect(Unit) {
        onChangeUiConfiguration(
            MainUIConfig(
                title = "",
                showNavigation = false,
                showTopBar = false,
                showBottomBar = false
            )
        )
    }

    val dataStoreRepository = koinInject<AppPreference>()

    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        dataStoreRepository.readUserId().collect { userId ->
            delay(2000) // Give time for animation
            if (userId.isNotEmpty()) {
                toHome()
            } else {
                toLogin()
            }
        }

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            // Background Image
            /*Image(
                painter = painterResource(Res.drawable.app_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            )*/

            // Gradient Overlay
            /* Box(
                 modifier = Modifier
                     .fillMaxSize()
                     .background(
                         Brush.verticalGradient(
                             colors = listOf(
                                 Color.Transparent,
                                 MaterialTheme.colorScheme.onPrimary,
                                 MaterialTheme.colorScheme.primary
                             )
                         )
                     )
             )*/

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppSpacing.Layout.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scaleAnim)
                        .alpha(alphaAnim),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.Size.lg))

                Text(
                    text = "ExpenseEntity Tracker",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.alpha(alphaAnim)
                )

            }

            // Bottom Text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }

}
