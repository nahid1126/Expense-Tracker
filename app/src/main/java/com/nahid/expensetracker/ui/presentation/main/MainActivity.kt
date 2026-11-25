package com.nahid.expensetracker.ui.presentation.main

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import com.nahid.expensetracker.domain.model.ThemeMode
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
import com.nahid.expensetracker.ui.presentation.component.CustomBottomNavigationBar
import com.nahid.expensetracker.ui.presentation.navigation.Destinations
import com.nahid.expensetracker.ui.presentation.navigation.NavGraph
import com.nahid.expensetracker.ui.theme.ExpenseTrackerTheme
import java.time.LocalTime
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            ExpenseTrackerTheme(darkTheme = darkTheme, dynamicColor = false) {
                App(viewModel, state, darkTheme)
            }
        }
    }
}
@SuppressLint("IntentReset")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    viewModel: MainViewModel,
    uiState: MainUiState,
    darkTheme: Boolean,
){
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val gmail = uiState.gmail
    var localHour by remember { mutableIntStateOf(LocalTime.now().hour) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { localHour = LocalTime.now().hour }

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            navController.navigate(Destinations.LoginScreen){
                popUpTo(0){
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }
    if (uiState.isLoading) {
        AnimatedProgressDialog()
    }
    if (uiState.showLogoutDialog) {
        ConfirmationDialog(
            title = "Sign out?",
            message = gmail?.let { "Sign out of $it?" } ?: "Sign out of Expense Tracker?",
            confirmText = "Sign out",
            onDismiss = {
                viewModel.updateUiState(uiState.copy(showLogoutDialog = false))
            },
            onConfirm = {
                viewModel.logout()
                viewModel.updateUiState(uiState.copy(showLogoutDialog = false))
            }
        )
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        floatingActionButton = {
            val config = uiState.uiConfig
            if (config.showFab) {
                FloatingActionButton(
                    onClick = config.onFabClick,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    config.fabContent.invoke()
                }
            }
        },
        topBar = {
            val config = uiState.uiConfig
            if (config.showTopBar) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                if (config.title == "Home") homeGreeting(localHour) else config.title,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (config.showSubTitle && config.subTitle.isNotBlank()) {
                                Text(config.subTitle, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    actions = {
                        if (config.showOptionMenu) {
                            ThemeSwitch(darkTheme) { checked ->
                                viewModel.setThemeMode(if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                            }
                            IconButton(onClick = { viewModel.updateUiState(uiState.copy(showLogoutDialog = true)) }) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign out")
                            }
                        }
                    },
                    navigationIcon = {
                        if (config.showNavigation) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                )
            }
        },
        bottomBar = {
            val showBottomBar = currentBackStack?.destination?.let { dest ->
                dest.hasRoute(Destinations.Home::class) || dest.hasRoute(Destinations.Transections::class)
            } ?: false

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {
                CustomBottomNavigationBar(
                    currentDestination = currentBackStack?.destination,
                    onItemClick = { destination ->
                        navController.navigate(destination) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onFabClick = {
                        navController.navigate(Destinations.AddExpense)
                    }
                )
            }
        },
    ) { innerPadding ->

        Box(Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)) {
            NavGraph(
                navController = navController,
                gmail = gmail,
                onChangeConfiguration = { config ->
                    viewModel.updateUiState(
                        uiState.copy(
                            uiConfig = config
                        )
                    )
                },
                onShowMessage = {
                    viewModel.showMessage(it)
                },
                onBottomNavigationChange = {
                }, onExit = {
                    (context as? Activity)?.finish()
                })
        }

    }

}


@Composable
private fun ThemeSwitch(darkTheme: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Switch(
        checked = darkTheme,
        onCheckedChange = onCheckedChange,
        modifier = Modifier.semantics { contentDescription = "Dark mode" },
        colors = SwitchDefaults.colors(
            uncheckedTrackColor = colors.primaryContainer,
            uncheckedThumbColor = colors.surfaceContainerLow,
            uncheckedBorderColor = colors.outlineVariant,
            uncheckedIconColor = colors.primary,
        ),
        thumbContent = {
            Icon(
                imageVector = if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        },
    )
}

private fun homeGreeting(hour: Int): String = when (hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    in 17..21 -> "Good evening"
    else -> "Welcome back"
}
