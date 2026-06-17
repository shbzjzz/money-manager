package com.wealthflow.app.presentation.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthflow.app.data.local.entity.AccountEntity
import com.wealthflow.app.data.local.entity.AccountType
import java.text.NumberFormat
import java.util.Locale

private fun aed(amount: Double) = "AED " + NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = 2; maximumFractionDigits = 2
}.format(amount)

@Composable
fun AccountsScreen(viewModel: AccountsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Vaults", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(16.dp)
        ) {
            Column {
                Text("NET WORTH", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(aed(state.netWorth), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Liabilities: ${aed(state.liabilities)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.accounts) { account -> AccountCard(account) }
        }
    }
}

@Composable
private fun AccountCard(account: AccountEntity) {
    val isLiability = account.type == AccountType.CREDIT_CARD
    Column(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(16.dp)
    ) {
        Box(
            Modifier.size(44.dp).clip(CircleShape)
                .background(if (isLiability) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Icon(
                when (account.type) {
                    AccountType.CASH -> Icons.Filled.AccountBalanceWallet
                    AccountType.BANK -> Icons.Filled.AccountBalance
                    AccountType.CREDIT_CARD -> Icons.Filled.CreditCard
                    AccountType.WALLET -> Icons.Filled.Smartphone
                },
                contentDescription = account.name,
                tint = if (isLiability) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(account.name, fontWeight = FontWeight.SemiBold)
        Text(account.type.name.replace("_", " "), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Text(
            (if (isLiability) "-" else "") + aed(account.balance),
            style = MaterialTheme.typography.titleLarge,
            color = if (isLiability) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}
