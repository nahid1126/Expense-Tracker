package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.ui.presentation.navigation.Destinations
import com.nahid.expensetracker.ui.theme.ExpenseTrackerTheme

@Composable
fun CustomBottomNavigationBar(
    currentDestination: NavDestination?,
    onItemClick: (Destinations) -> Unit,
    onFabClick: () -> Unit,
) {
    val selected = when {
        currentDestination?.hasRoute(Destinations.Home::class) == true -> Destinations.Home
        currentDestination?.hasRoute(Destinations.Transections::class) == true -> Destinations.Transections
        else -> null
    }
    NavigationDock(selected, onItemClick, onFabClick)
}

@Composable
private fun NavigationDock(
    selected: Destinations?,
    onItemClick: (Destinations) -> Unit,
    onAdd: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.Layout.screenPadding, vertical = AppSpacing.Size.md)
            .background(colors.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = BottomNavCutoutShape(),
            color = colors.surfaceContainerLow,
            border = BorderStroke(1.dp, colors.outlineVariant),
            shadowElevation = 3.dp,
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 0.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NavBarItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    selected = selected == Destinations.Home,
                    onClick = { onItemClick(Destinations.Home) },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(58.dp))
                NavBarItem(
                    label = "Transactions",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    selected = selected == Destinations.Transections,
                    onClick = { onItemClick(Destinations.Transections) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Surface(
            onClick = onAdd,
            color = Color.Transparent,
            shape = RoundedCornerShape(25.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = colors.primary,
                    contentColor = colors.onPrimary,
                    border = BorderStroke(2.dp, colors.surfaceContainerLow),
                    shadowElevation = 4.dp,
                ) {
                    Box(Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Text(
                    "Add", color = colors.primary, style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val indicator by animateColorAsState(
        if (selected) colors.primaryContainer else Color.Transparent,
        label = "Tab background",
    )
    val foreground by animateColorAsState(
        if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        label = "Tab foreground",
    )
    Column(
        modifier
            .clip(RoundedCornerShape(21.dp))
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .heightIn(min = 52.dp)
            .padding(horizontal = 4.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
    ) {
        Box(
            Modifier
                .width(52.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, Modifier.size(24.dp), tint = foreground)
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = foreground,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "Navigation dock", widthDp = 393)
@Composable
private fun NavigationDockPreview() {
    ExpenseTrackerTheme(darkTheme = false, dynamicColor = false) {
        NavigationDock(Destinations.Transections, onItemClick = {}, onAdd = {})
    }
}
