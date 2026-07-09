package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nahid.expensetracker.ui.presentation.navigation.Destinations
import com.nahid.expensetracker.ui.theme.Gray
import com.nahid.expensetracker.ui.theme.PurpleSageBush

@Composable
fun CustomBottomNavigationBar(
    currentRoute: String?,
    onItemClick: (Destinations) -> Unit,
    onFabClick: () -> Unit
) {
    val activeColor = Color(0xFF2B5748)
    val inactiveColor = Gray // Grayish like in image

    val leftItems = listOf(
        BottomNavItem(Destinations.Home, Icons.Default.Home, "Home"),
        BottomNavItem(Destinations.Stats, Icons.Default.BarChart, "Stats")
    )
    val rightItems = listOf(
        BottomNavItem(Destinations.Wallet, Icons.Default.Wallet, "Wallet"),
        BottomNavItem(Destinations.Profile, Icons.Default.Person, "Profile")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Transparent)
    ) {
        // Main Bar with Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 8.dp,
                    shape = BottomNavCutoutShape(),
                    clip = false
                )
                .clip(BottomNavCutoutShape())
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Items
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    leftItems.forEach { item ->
                        NavBarIcon(
                            item = item,
                            isSelected = currentRoute?.contains(item.destination::class.simpleName ?: "") == true,
                            activeColor = activeColor,
                            inactiveColor = inactiveColor,
                            onClick = { onItemClick(item.destination) }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(70.dp))

                // Right Items
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    rightItems.forEach { item ->
                        NavBarIcon(
                            item = item,
                            isSelected = currentRoute?.contains(item.destination::class.simpleName ?: "") == true,
                            activeColor = activeColor,
                            inactiveColor = inactiveColor,
                            onClick = { onItemClick(item.destination) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onFabClick,
            shape = CircleShape,
            containerColor = activeColor,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(4.dp, 4.dp),
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.TopCenter)
                .offset(y = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavBarIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // In the image, there is a subtle background for active icon sometimes, 
        // but here we just use color for simplicity or add a small indicator.
        Icon(
            imageVector = item.icon,
            contentDescription = item.contentDescription,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

private data class BottomNavItem(
    val destination: Destinations,
    val icon: ImageVector,
    val contentDescription: String
)
