package com.wealthflow.app.presentation.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthflow.app.data.local.entity.BudgetEntity
import com.wealthflow.app.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(repository: FinanceRepository) : ViewModel() {
    private val currentMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
    val budgets = repository.observeBudgets(currentMonth)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@Composable
fun BudgetScreen(viewModel: BudgetViewModel = hiltViewModel()) {
    val budgets by viewModel.budgets.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Monthly Budgets", style = MaterialTheme.typography.headlineMedium)
        if (budgets.isEmpty()) {
            Text("No budgets set yet.", style = MaterialTheme.typography.bodyMedium)
        }
        budgets.forEach { b -> BudgetRow(b) }
    }
}

@Composable
private fun BudgetRow(budget: BudgetEntity) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("Category #${budget.categoryId}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = 0.4f, // placeholder; wire to actual spend vs limitAmount when category spend query is added
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
        )
        Text("Limit: AED ${budget.limitAmount}", style = MaterialTheme.typography.labelMedium)
    }
}
