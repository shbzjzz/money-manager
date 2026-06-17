package com.wealthflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AccountType { CASH, BANK, CREDIT_CARD, WALLET }

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val currency: String = "AED",
    val isSynced: Boolean = false
)
