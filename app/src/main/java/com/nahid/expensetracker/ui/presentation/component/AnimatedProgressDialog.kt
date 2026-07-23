package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nahid.expensetracker.R
import com.nahid.expensetracker.core.AppConstants
import kotlinx.coroutines.delay

@Composable
fun AnimatedProgressDialog(modifier: Modifier = Modifier.size(100.dp)) {
    val animateDegree = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5)
            animateDegree.animateTo(
                animateDegree.value + 30 % 360,
                animationSpec = tween(easing = LinearEasing)
            )
        }
    }

    Dialog(onDismissRequest = {

    }, properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
                trackColor = LightGray,
                strokeCap = StrokeCap.Round
            )

            Image(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding((AppConstants.APP_MARGIN * 2).dp)
                    .graphicsLayer {
                        rotationY = animateDegree.value
                    }
            )
        }
    }
}

@Composable
fun AnimatedLottieProgressDialog(modifier: Modifier = Modifier.size(100.dp)) {
    Dialog(onDismissRequest = {

    }, properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            val preloaderLottieComposition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(
                    R.raw.loading
                )
            )

            val preloaderProgress by animateLottieCompositionAsState(
                preloaderLottieComposition,
                iterations = LottieConstants.IterateForever,
                isPlaying = true
            )

            LottieAnimation(
                composition = preloaderLottieComposition,
                progress = preloaderProgress,
                modifier = modifier
            )
        }
    }
}