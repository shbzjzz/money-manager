package com.wealthflow.app.data.local.dao

import androidx.room.*
import com.wealthflow.app.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE month = :month")
    fun observeForMonth(month: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: BudgetEntity): Long

    @Delete
    suspend fun delete(budget: BudgetEntity)
}
