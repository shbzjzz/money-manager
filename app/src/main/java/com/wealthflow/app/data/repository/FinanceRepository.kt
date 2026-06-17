package com.wealthflow.app.data.repository

import com.wealthflow.app.data.local.dao.*
import com.wealthflow.app.data.local.entity.*
import com.wealthflow.app.data.remote.ApiService
import com.wealthflow.app.data.remote.dto.TransactionDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val emiDao: EmiDao,
    private val api: ApiService
) {
    // ---------- Accounts ----------
    fun observeAccounts(): Flow<List<AccountEntity>> = accountDao.observeAll()
    fun observeNetAssets(): Flow<Double> = accountDao.observeNetAssets()
    fun observeLiabilities(): Flow<Double> = accountDao.observeLiabilities()
    suspend fun upsertAccount(account: AccountEntity) = accountDao.upsert(account)
    suspend fun seedDefaultAccountIfEmpty() {
        if (accountDao.count() == 0) {
            accountDao.upsert(AccountEntity(name = "Cash", type = AccountType.CASH, balance = 0.0))
        }
    }

    // ---------- Categories ----------
    fun observeCategories(kind: CategoryKind) = categoryDao.observeByKind(kind)
    fun observeAllCategories() = categoryDao.observeAll()
    suspend fun seedDefaultCategoriesIfEmpty() {
        categoryDao.insertDefaults(defaultCategories())
    }

    // ---------- Transactions ----------
    fun observeRecentTransactions(limit: Int = 20) = transactionDao.observeRecent(limit)
    fun observeTotalByType(type: TxType, start: Long, end: Long) =
        transactionDao.observeTotalByType(type, start, end)
    fun observeCategoryTotals(start: Long, end: Long) =
        transactionDao.observeCategoryTotals(start, end)
    fun searchTransactions(query: String) = transactionDao.search(query)

    /** Local-first write: insert into Room, adjust account balance, fire-and-forget sync to Worker/D1 */
    suspend fun addTransaction(tx: TransactionEntity) {
        transactionDao.insert(tx)
        val delta = when (tx.type) {
            TxType.INCOME -> tx.amount
            TxType.EXPENSE -> -tx.amount
            TxType.TRANSFER -> -tx.amount
        }
        accountDao.adjustBalance(tx.accountId, delta)
        if (tx.type == TxType.TRANSFER && tx.toAccountId != null) {
            accountDao.adjustBalance(tx.toAccountId, tx.amount)
        }
        runCatching {
            api.createTransaction(
                TransactionDto(
                    amount = tx.amount,
                    type = tx.type.name,
                    categoryId = tx.categoryId,
                    accountId = tx.accountId,
                    toAccountId = tx.toAccountId,
                    date = tx.date,
                    note = tx.note
                )
            )
        } // ignore failure - stays queued as isSynced=false, retried by SyncWorker
    }

    suspend fun deleteTransaction(tx: TransactionEntity) {
        transactionDao.delete(tx)
        runCatching { api.deleteTransaction(tx.id) }
    }

    // ---------- Budgets ----------
    fun observeBudgets(month: String) = budgetDao.observeForMonth(month)
    suspend fun upsertBudget(budget: BudgetEntity) = budgetDao.upsert(budget)

    // ---------- EMI ----------
    fun observeEmis() = emiDao.observeAll()
    suspend fun upsertEmi(emi: EmiEntity) = emiDao.upsert(emi)

    private fun defaultCategories() = listOf(
        CategoryEntity(name = "Food & Dining", kind = CategoryKind.EXPENSE, icon = "restaurant", color = "#064e3b"),
        CategoryEntity(name = "Transport", kind = CategoryKind.EXPENSE, icon = "directions_car", color = "#2b6954"),
        CategoryEntity(name = "Rent", kind = CategoryKind.EXPENSE, icon = "home", color = "#80bea6"),
        CategoryEntity(name = "Bills", kind = CategoryKind.EXPENSE, icon = "receipt_long", color = "#95d3ba"),
        CategoryEntity(name = "Shopping", kind = CategoryKind.EXPENSE, icon = "shopping_cart", color = "#404944"),
        CategoryEntity(name = "Salary", kind = CategoryKind.INCOME, icon = "payments", color = "#565e74")
    )
}
