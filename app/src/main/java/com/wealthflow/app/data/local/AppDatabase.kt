package com.wealthflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.wealthflow.app.data.local.dao.*
import com.wealthflow.app.data.local.entity.*

class Converters {
    @TypeConverter fun toAccountType(v: String) = AccountType.valueOf(v)
    @TypeConverter fun fromAccountType(v: AccountType) = v.name

    @TypeConverter fun toCategoryKind(v: String) = CategoryKind.valueOf(v)
    @TypeConverter fun fromCategoryKind(v: CategoryKind) = v.name

    @TypeConverter fun toTxType(v: String) = TxType.valueOf(v)
    @TypeConverter fun fromTxType(v: TxType) = v.name
}

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        EmiEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun emiDao(): EmiDao

    companion object {
        const val NAME = "wealthflow.db"
    }
}
