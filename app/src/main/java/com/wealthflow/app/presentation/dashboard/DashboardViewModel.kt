package com.wealthflow.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthflow.app.data.local.dao.TransactionWithDetails
import com.wealthflow.app.domain.usecase.GetDashboardSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<TransactionWithDetails> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardSummary: GetDashboardSummaryUseCase
) : ViewModel() {

    private val monthRange = monthStartEnd()

    val uiState = combine(
        getDashboardSummary.netAssets(),
        getDashboardSummary.monthlyIncome(monthRange.first, monthRange.second),
        getDashboardSummary.monthlyExpense(monthRange.first, monthRange.second),
        getDashboardSummary.recentTransactions(10)
    ) { net, income, expense, recent ->
        DashboardUiState(
            totalBalance = net,
            monthlyIncome = income,
            monthlyExpense = expense,
            recentTransactions = recent
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    private fun monthStartEnd(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis
        return start to end
    }
}
