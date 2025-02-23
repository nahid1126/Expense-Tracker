package com.nahid.expensetracker.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nahid.expensetracker.R
import com.nahid.expensetracker.ui.theme.Zinc

@Composable
fun NavHostScreen() {
    val rememberNavController = rememberNavController()
    var bottomBarVisibility by remember { mutableStateOf(true) }
    Scaffold(bottomBar = {
        AnimatedVisibility(visible = bottomBarVisibility){
            BottomNavigationBar(
                navController = rememberNavController,
                items = listOf(
                    NavItem(route = "/home", icon = R.drawable.home),
                    NavItem(route = "/stats", icon = R.drawable.stats)
                )
            )
        }
    }) {
        NavHost(
            navController = rememberNavController,
            startDestination = "/home",
            modifier = Modifier.padding(it)
        ) {
            composable(route = "/home") {
                bottomBarVisibility = true
                HomeScreen(rememberNavController)
            }
            composable(route = "add/{id}") {
                bottomBarVisibility = false
                val expenseId = it.arguments?.getString("id")?.toIntOrNull()
                AddExpenseScreen(rememberNavController, expenseId)
            }
            composable(route = "/stats") {
                bottomBarVisibility = true
                StatsScreen(rememberNavController)
            }
        }
    }

}

data class NavItem(val route: String, val icon: Int)

@Composable
fun BottomNavigationBar(navController: NavController, items: List<NavItem>) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomAppBar {
        items.forEach {
            NavigationBarItem(
                selected = currentRoute == it.route,
                onClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(painter = painterResource(id = it.icon), contentDescription = null) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Zinc,
                    selectedTextColor = Zinc,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}