package com.wealthflow.app.data.local.dao

import androidx.room.*
import com.wealthflow.app.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(account: AccountEntity): Long

    @Query("UPDATE accounts SET balance = balance + :delta WHERE id = :id")
    suspend fun adjustBalance(id: Long, delta: Double)

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun count(): Int

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT COALESCE(SUM(balance),0) FROM accounts WHERE type != 'CREDIT_CARD'")
    fun observeNetAssets(): Flow<Double>

    @Query("SELECT COALESCE(SUM(balance),0) FROM accounts WHERE type = 'CREDIT_CARD'")
    fun observeLiabilities(): Flow<Double>
}
