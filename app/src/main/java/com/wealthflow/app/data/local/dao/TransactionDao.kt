package com.wealthflow.app.data.local.dao

import androidx.room.*
import com.wealthflow.app.data.local.entity.TransactionEntity
import com.wealthflow.app.data.local.entity.TxType
import kotlinx.coroutines.flow.Flow

data class TransactionWithDetails(
    val id: Long,
    val amount: Double,
    val type: TxType,
    val date: Long,
    val note: String,
    val categoryName: String?,
    val categoryIcon: String?,
    val accountName: String
)

data class CategoryTotal(val categoryName: String, val color: String, val total: Double)

@Dao
interface TransactionDao {

    @Query(
        """
        SELECT t.id, t.amount, t.type, t.date, t.note,
               c.name as categoryName, c.icon as categoryIcon,
               a.name as accountName
        FROM transactions t
        LEFT JOIN categories c ON c.id = t.categoryId
        JOIN accounts a ON a.id = t.accountId
        ORDER BY t.date DESC
        LIMIT :limit
        """
    )
    fun observeRecent(limit: Int = 20): Flow<List<TransactionWithDetails>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = :type AND date BETWEEN :start AND :end")
    fun observeTotalByType(type: TxType, start: Long, end: Long): Flow<Double>

    @Query(
        """
        SELECT c.name as categoryName, c.color as color, SUM(t.amount) as total
        FROM transactions t JOIN categories c ON c.id = t.categoryId
        WHERE t.type = 'EXPENSE' AND t.date BETWEEN :start AND :end
        GROUP BY c.id ORDER BY total DESC
        """
    )
    fun observeCategoryTotals(start: Long, end: Long): Flow<List<CategoryTotal>>

    @Insert
    suspend fun insert(tx: TransactionEntity): Long

    @Update
    suspend fun update(tx: TransactionEntity)

    @Delete
    suspend fun delete(tx: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query(
        """
        SELECT t.id, t.amount, t.type, t.date, t.note,
               c.name as categoryName, c.icon as categoryIcon,
               a.name as accountName
        FROM transactions t
        LEFT JOIN categories c ON c.id = t.categoryId
        JOIN accounts a ON a.id = t.accountId
        WHERE t.note LIKE '%' || :query || '%'
           OR c.name LIKE '%' || :query || '%'
        ORDER BY t.date DESC
        """
    )
    fun search(query: String): Flow<List<TransactionWithDetails>>
}
