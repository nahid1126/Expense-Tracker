package com.nahid.expensetracker.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavHostScreen() {
    val rememberNavController = rememberNavController()
    NavHost(navController = rememberNavController, startDestination = "/home") {
        composable(route = "/home") {
            HomeScreen(rememberNavController)
        }
        composable(route = "/add") {
            AddExpenseScreen(rememberNavController)
        }
    }
}