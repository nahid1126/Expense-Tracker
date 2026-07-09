package com.nahid.expensetracker.domain.uiconfig

import androidx.compose.runtime.Composable

data class MainUIConfig(
    val title: String = "",
    val showDateTitle: Boolean = true,
    val showSubTitle: Boolean = false,
    val dateTitle: String = "",
    val subTitle: String = "",
    val showTopBar: Boolean = false,
    val showBottomBar: Boolean = false,
    val showFab: Boolean = false,
    val showOptionMenu: Boolean = false,
    val showNavigation: Boolean = false,
    val onFabClick:(() -> Unit) = {},
    val fabContent:(@Composable () -> Unit) = {},
)
