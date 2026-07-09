package com.nahid.expensetracker.ui.presentation.main

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.core.utils.extension.toFormat
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
import com.nahid.expensetracker.ui.presentation.navigation.Destinations
import com.nahid.expensetracker.ui.presentation.navigation.NavGraph
import com.nahid.expensetracker.ui.theme.ExpenseTrackerTheme
import com.nahid.expensetracker.ui.theme.Typography
import com.nahid.expensetracker.ui.theme.WarningOrange
import org.koin.compose.viewmodel.koinViewModel

private var keepSplash = false
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            ExpenseTrackerTheme(darkTheme = state.isDarkMode, dynamicColor = false) {
                App(viewModel, state)
            }
        }
    }
}
@SuppressLint("IntentReset")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    viewModel: MainViewModel,
    uiState: MainUiState
){
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val currentRoute = currentBackStack?.destination?.route
    val snackBarHostState = remember { SnackbarHostState() }
    val currentUser = uiState.user

    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning,"
            in 12..15 -> "Good Afternoon,"
            in 16..20 -> "Good Evening,"
            else -> "Good Night,"
        }
    }
    var expanded by remember { mutableStateOf(false) }
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
    var rotateDegree = remember { Animatable(0f) }
    LaunchedEffect(currentRoute) {
        rotateDegree.snapTo(0f)
        rotateDegree.animateTo(180f, animationSpec = tween(500, easing = LinearEasing))
    }
    if (uiState.showLogoutDialog) {
        ConfirmationDialog(
            title = "Logout",
            message = "Are You Sure Want to Logout ?",
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
            if (uiState.uiConfig.showTopBar) {
                TopAppBar(
                    title = {
                        val title = uiState.uiConfig.title
                        val dateTitle = uiState.uiConfig.dateTitle.ifEmpty { System.currentTimeMillis().toFormat("MMM dd, yyyy") }
                        val subTitle = uiState.uiConfig.subTitle
                        Column {
                            if (title.contains("Home")) {
                                Text(
                                    greeting,
                                    style = Typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            if (title.contains("Home")) {
                                Spacer(modifier = Modifier.size(AppSpacing.Size.md))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "${currentUser?.role?:""}, ${currentUser?.userName}",
                                        style = Typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(end = AppSpacing.Size.sm),
                                    )
                                    Icon(Icons.Default.WavingHand,contentDescription = null, tint = WarningOrange)
                                }
                            } else {
                                Spacer(modifier = Modifier.size(AppSpacing.Size.md))
                                Text(
                                    title,
                                    style = Typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }


                            if (uiState.uiConfig.showSubTitle) {
                                Spacer(modifier = Modifier.size(AppSpacing.Size.sm))
                                Text(
                                    subTitle,
                                    style = Typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            if (uiState.uiConfig.showDateTitle) {
                                Spacer(modifier = Modifier.size(AppSpacing.Size.sm))
                                Text(
                                    dateTitle,
                                    style = Typography.titleMedium.copy(),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            Spacer(modifier = Modifier.size(AppSpacing.Size.md))

                        }


                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        if (uiState.uiConfig.showOptionMenu) {
                            Row {

                                Box {
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = null,
                                            tint = White
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("About") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Outlined.Info,
                                                    contentDescription = null
                                                )
                                            },
                                            onClick = {
                                                expanded = !expanded
                                                navController.navigate(Destinations.AboutApp)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Logout") },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Outlined.Logout,
                                                    contentDescription = null
                                                )
                                            },
                                            onClick = {
                                                expanded = !expanded
                                                viewModel.updateUiState(
                                                    uiState.copy(
                                                        showLogoutDialog = true
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                            }

                        }
                    }, navigationIcon = {
                        if (uiState.uiConfig.showNavigation) {
                            IconButton(
                                onClick = {
                                    val entry = navController.previousBackStackEntry
                                    if (entry != null) {
                                        navController.popBackStack()
                                    }
                                }, modifier = Modifier
                                    .padding(
                                        end = (AppConstants.APP_MARGIN / 2).dp
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.graphicsLayer {
                                        rotationZ = rotateDegree.value
                                    }
                                )
                            }


                        }
                    }, modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                )
            }

        },
    ) { innerPadding ->

        Box(Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                currentUser = currentUser,
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
