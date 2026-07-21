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
    data object AddExpense :Destinations()

    @Serializable
    data class UpdateExpense(val expenseJson: String) : Destinations()

    @Serializable
    data object AboutApp :Destinations()


    /*@Serializable
    data class WalletDetails(val customerJson: String) : Destinations()*/

}