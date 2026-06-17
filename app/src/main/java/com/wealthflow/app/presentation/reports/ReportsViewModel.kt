package com.wealthflow.app.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthflow.app.data.local.dao.CategoryTotal
import com.wealthflow.app.data.local.entity.TxType
import com.wealthflow.app.domain.usecase.GetDashboardSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class ReportsUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categoryTotals: List<CategoryTotal> = emptyList()
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    summary: GetDashboardSummaryUseCase,
    repository: com.wealthflow.app.data.repository.FinanceRepository
) : ViewModel() {

    private val range = monthRange()

    val uiState = combine(
        summary.monthlyIncome(range.first, range.second),
        summary.monthlyExpense(range.first, range.second),
        repository.observeCategoryTotals(range.first, range.second)
    ) { income, expense, totals ->
        ReportsUiState(income, expense, totals)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportsUiState())

    private fun monthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        return start to cal.timeInMillis
    }
}
