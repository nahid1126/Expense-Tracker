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
import androidx.navigation.toRoute
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.addexpense.AddExpenseScreen
import com.nahid.expensetracker.ui.presentation.auth.login.LoginScreen
import com.nahid.expensetracker.ui.presentation.auth.splash.SplashScreen
import com.nahid.expensetracker.ui.presentation.home.HomeScreen
import com.nahid.expensetracker.ui.presentation.transections.TransectionsScreen
import com.nahid.expensetracker.ui.presentation.updateexpense.UpdateExpenseScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    gmail: String?,
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
            gmail = gmail,
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

    composable<Destinations.LoginScreen>(enterTransition = { defaultEnterTransition() }) {
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
    gmail: String?,
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
        if (gmail != null) {
            HomeScreen(
                user = gmail,
                onChangeConfiguration = onChangeConfiguration,
                onShowMessage = onShowMessage,
                toExit = { onExit() },
                onSeeAllClicked = {
                    navController.navigate(Destinations.Transections)
                },
                onUpdate = { expense ->
                    val expenseJson = Json.encodeToString(expense)
                    navController.navigate(Destinations.UpdateExpense(expenseJson))
                }
            )
        }
    }

    composable<Destinations.Transections>(enterTransition = { defaultEnterTransition() }) {
        TransectionsScreen(
            onChangeConfiguration = onChangeConfiguration,
            onShowMessage = onShowMessage,
            onBack = {
                navController.navigateUp()
            },
            onUpdate = { expense ->
                val expenseJson = Json.encodeToString(expense)
                navController.navigate(Destinations.UpdateExpense(expenseJson))
            }
        )
    }
    composable<Destinations.AddExpense>(enterTransition = { defaultEnterTransition() }) {
        AddExpenseScreen(
            onChangeConfiguration = onChangeConfiguration,
            onShowMessage = onShowMessage, onBack = {
                navController.navigateUp()
            })
    }

    composable<Destinations.UpdateExpense>(enterTransition = { defaultEnterTransition() }) {
        val expenseRoute = it.toRoute<Destinations.UpdateExpense>()
        val expenseJson = expenseRoute.expenseJson
        val expense = Json.decodeFromString<Expense>(expenseJson)
        UpdateExpenseScreen(
            onChangeConfiguration = onChangeConfiguration,
            expense = expense,
            onShowMessage = onShowMessage, onBack = {
                navController.navigateUp()
            })
    }
}
