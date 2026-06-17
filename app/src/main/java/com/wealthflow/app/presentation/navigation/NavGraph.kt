package com.wealthflow.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wealthflow.app.presentation.accounts.AccountsScreen
import com.wealthflow.app.presentation.addtransaction.AddTransactionScreen
import com.wealthflow.app.presentation.budget.BudgetScreen
import com.wealthflow.app.presentation.components.NavItem
import com.wealthflow.app.presentation.components.WealthFlowBottomNav
import com.wealthflow.app.presentation.dashboard.DashboardScreen
import com.wealthflow.app.presentation.reports.ReportsScreen

private const val ADD_TRANSACTION_ROUTE = "add_transaction"

@Composable
fun WealthFlowNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != ADD_TRANSACTION_ROUTE) {
                WealthFlowBottomNav(
                    currentRoute = currentRoute ?: NavItem.Dashboard.route,
                    onSelect = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(Modifier) {
            NavHost(
                navController = navController,
                startDestination = NavItem.Dashboard.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(NavItem.Dashboard.route) {
                    DashboardScreen(onAddTransaction = { navController.navigate(ADD_TRANSACTION_ROUTE) })
                }
                composable(NavItem.Activity.route) {
                    // Activity tab reuses Dashboard's transaction list; can be extended into full history screen
                    DashboardScreen(onAddTransaction = { navController.navigate(ADD_TRANSACTION_ROUTE) })
                }
                composable(NavItem.Reports.route) { ReportsScreen() }
                composable(NavItem.Vaults.route) { AccountsScreen() }
                composable(NavItem.Settings.route) { BudgetScreen() }
                composable(ADD_TRANSACTION_ROUTE) {
                    AddTransactionScreen(onClose = { navController.popBackStack() })
                }
            }
        }
    }
}
