package com.wealthflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class TxType { INCOME, EXPENSE, TRANSFER }

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(entity = AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"]),
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TxType,
    val categoryId: Long?,
    val accountId: Long,
    val toAccountId: Long? = null, // used when type = TRANSFER
    val date: Long,                // epoch millis
    val note: String = "",
    val receiptImagePath: String? = null,
    val isSynced: Boolean = false
)
