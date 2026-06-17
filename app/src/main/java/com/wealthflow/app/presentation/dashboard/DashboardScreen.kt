package com.wealthflow.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthflow.app.data.local.dao.TransactionWithDetails
import com.wealthflow.app.data.local.entity.TxType
import java.text.NumberFormat
import java.util.Locale

private fun aed(amount: Double): String =
    "AED " + NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }.format(amount)

@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { HeroBalanceCard(state) }
            item {
                Text(
                    "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            items(state.recentTransactions) { tx -> TransactionRow(tx) }
        }

        FloatingActionButton(
            onClick = onAddTransaction,
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add transaction")
        }
    }
}

@Composable
private fun HeroBalanceCard(state: DashboardUiState) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                "TOTAL BALANCE",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                aed(state.totalBalance),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip(
                    icon = Icons.Filled.ArrowDownward,
                    label = "Income",
                    value = aed(state.monthlyIncome),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    icon = Icons.Filled.ArrowUpward,
                    label = "Expense",
                    value = aed(state.monthlyExpense),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.secondaryContainer) }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
            Text(value, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TransactionRow(tx: TransactionWithDetails) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(tx.categoryName ?: "Transfer", fontWeight = FontWeight.Medium)
                Text(tx.accountName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        val isIncome = tx.type == TxType.INCOME
        Text(
            (if (isIncome) "+" else "-") + aed(tx.amount),
            color = if (isIncome) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}
