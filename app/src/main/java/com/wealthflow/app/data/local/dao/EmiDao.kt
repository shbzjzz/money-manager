package com.wealthflow.app.data.local.dao

import androidx.room.*
import com.wealthflow.app.data.local.entity.EmiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmiDao {
    @Query("SELECT * FROM emis ORDER BY dueDate ASC")
    fun observeAll(): Flow<List<EmiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(emi: EmiEntity): Long

    @Delete
    suspend fun delete(emi: EmiEntity)
}
