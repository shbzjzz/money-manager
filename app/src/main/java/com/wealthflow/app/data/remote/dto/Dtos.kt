package com.wealthflow.app.data.remote.dto

data class AccountDto(
    val id: Long? = null,
    val name: String,
    val type: String,
    val balance: Double,
    val currency: String = "AED"
)

data class TransactionDto(
    val id: Long? = null,
    val amount: Double,
    val type: String,
    val categoryId: Long?,
    val accountId: Long,
    val toAccountId: Long? = null,
    val date: Long,
    val note: String = ""
)

data class CategoryDto(
    val id: Long? = null,
    val name: String,
    val kind: String,
    val icon: String,
    val color: String
)

data class SyncPushRequest(
    val accounts: List<AccountDto>,
    val transactions: List<TransactionDto>
)

data class SyncPullResponse(
    val accounts: List<AccountDto>,
    val categories: List<CategoryDto>,
    val transactions: List<TransactionDto>
)
