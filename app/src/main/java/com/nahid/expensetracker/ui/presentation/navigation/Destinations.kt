package com.nahid.expensetracker.ui.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Destinations {
    @Serializable
    data object SplashScreen :Destinations()
    @Serializable
    data object LoginScreen :Destinations()
    @Serializable
    data object Home :Destinations()

    @Serializable
    data object Transections :Destinations()

    @Serializable
    data object Wallet :Destinations()

    @Serializable
    data object Profile :Destinations()

    @Serializable
    data object AddExpense :Destinations()

    @Serializable
    data object AboutApp :Destinations()

    @Serializable
    data object Expense :Destinations()

    @Serializable
    data object UpdateExpense :Destinations()


    /*@Serializable
    data class WalletDetails(val customerJson: String) : Destinations()*/

}