package com.wealthflow.app.domain.usecase

import com.wealthflow.app.data.local.entity.TransactionEntity
import com.wealthflow.app.data.repository.FinanceRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repo: FinanceRepository
) {
    suspend operator fun invoke(tx: TransactionEntity) {
        require(tx.amount > 0) { "Amount must be > 0" }
        repo.addTransaction(tx)
    }
}

class GetDashboardSummaryUseCase @Inject constructor(
    private val repo: FinanceRepository
) {
    fun netAssets() = repo.observeNetAssets()
    fun liabilities() = repo.observeLiabilities()
    fun recentTransactions(limit: Int = 10) = repo.observeRecentTransactions(limit)
    fun monthlyIncome(start: Long, end: Long) =
        repo.observeTotalByType(com.wealthflow.app.data.local.entity.TxType.INCOME, start, end)
    fun monthlyExpense(start: Long, end: Long) =
        repo.observeTotalByType(com.wealthflow.app.data.local.entity.TxType.EXPENSE, start, end)
}
