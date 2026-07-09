package com.nahid.expensetracker.ui.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nahid.expensetracker.domain.model.User
import com.nahid.expensetracker.ui.presentation.auth.login.LoginScreen
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.auth.splash.SplashScreen
import com.nahid.expensetracker.ui.presentation.home.HomeScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    currentUser: User?,
    startDestinations: Destinations = Destinations.SplashScreen,
    onBottomNavigationChange: (Int) -> Unit,
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
    onExit: () -> Unit,
) {
    NavHost(navController = navController, startDestination = startDestinations) {
        authGraph(navController, onChangeConfiguration, onShowMessage)
        
        homeGraph(
            navController = navController,
            currentUser = currentUser,
            onChangeConfiguration = onChangeConfiguration,
            onShowMessage = onShowMessage,
            onExit = onExit
        )

    }
}

private fun defaultEnterTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(1000),
        initialOffsetX = { it }
    )
}

private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onChangeUiConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit
) {
    composable<Destinations.SplashScreen> {
        SplashScreen(
            toHome = {
                navController.navigate(Destinations.Home) {
                    popUpTo<Destinations.SplashScreen> { inclusive = true }
                }
            },
            toLogin = {
                navController.navigate(Destinations.LoginScreen) {
                    popUpTo<Destinations.SplashScreen> { inclusive = true }
                }
            },
            onChangeUiConfiguration = onChangeUiConfiguration
        )
    }

    composable<Destinations.LoginScreen> {
        LoginScreen(
            toHome = {
                navController.navigate(Destinations.Home) {
                    popUpTo<Destinations.LoginScreen> { inclusive = true }
                }
            },
            onChangeUiConfiguration = onChangeUiConfiguration,
            onShowMessage = onShowMessage
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
private fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    currentUser: User?,
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
    onExit: () -> Unit,
) {
    composable<Destinations.Home>(enterTransition = {
        scaleIn(
            animationSpec = tween(500),
            transformOrigin = TransformOrigin.Center
        )
    }) {
        if (currentUser != null) {
            HomeScreen(
                user = currentUser,
                onChangeConfiguration = onChangeConfiguration,
                onShowMessage = onShowMessage
            )
        }
    }
}




/*private fun NavGraphBuilder.reportGraph(
    currentUser: User?,
    navController: NavHostController,
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit
) {
    composable<Destinations.Target>(enterTransition = { defaultEnterTransition() }) {
        if (currentUser != null) {
            TargetScreen(
                user = currentUser,
                onChangeConfiguration = onChangeConfiguration,
                onShowMessage = onShowMessage
            )
        }
    }

    composable<Destinations.ProductSummary>(enterTransition = { defaultEnterTransition() }) {
        if (currentUser != null) {
            ProductSummaryScreen(
                user = currentUser,
                onChangeConfiguration = onChangeConfiguration,
                onShowMessage = onShowMessage
            )
        }
    }

    composable<Destinations.Program>(enterTransition = { defaultEnterTransition() }) {
        if (currentUser != null) {
            ProgramScreen(
                user = currentUser,
                onChangeConfiguration = onChangeConfiguration,
                onShowMessage = onShowMessage,
                onNavigateToPdfViewer = { filePath ->
                    navController.navigate(Destinations.PdfViewer(filePath))
                }
            )
        }
    }

    composable<Destinations.DataAnalysis>(enterTransition = { defaultEnterTransition() }) {
       *//* DataAnalysisScreen(
            onChangeConfiguration = onChangeConfiguration,
            onShowMessage = onShowMessage
        )*//*
    }
}*/
