package com.wealthflow.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : NavItem("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Activity : NavItem("activity", "Activity", Icons.Filled.ReceiptLong)
    object Reports : NavItem("reports", "Reports", Icons.Filled.BarChart)
    object Vaults : NavItem("vaults", "Vaults", Icons.Filled.AccountBalanceWallet)
    object Settings : NavItem("settings", "Settings", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    NavItem.Dashboard, NavItem.Activity, NavItem.Reports, NavItem.Vaults, NavItem.Settings
)

@Composable
fun WealthFlowBottomNav(currentRoute: String, onSelect: (String) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, style = MaterialTheme.typography.labelMedium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
