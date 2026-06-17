package com.wealthflow.app.data.local.dao

import androidx.room.*
import com.wealthflow.app.data.local.entity.CategoryEntity
import com.wealthflow.app.data.local.entity.CategoryKind
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE kind = :kind")
    fun observeByKind(kind: CategoryKind): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaults(categories: List<CategoryEntity>)

    @Delete
    suspend fun delete(category: CategoryEntity)
}
