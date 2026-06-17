package com.wealthflow.app.data.remote

import com.wealthflow.app.data.remote.dto.*
import retrofit2.http.*

/**
 * Hits Cloudflare Worker which itself reads/writes D1 (SQLite at edge).
 * Worker source: /worker/index.js in this project.
 */
interface ApiService {

    @GET("transactions")
    suspend fun getTransactions(): List<TransactionDto>

    @POST("transactions")
    suspend fun createTransaction(@Body tx: TransactionDto): TransactionDto

    @PUT("transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: Long, @Body tx: TransactionDto): TransactionDto

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long)

    @GET("accounts")
    suspend fun getAccounts(): List<AccountDto>

    @POST("accounts")
    suspend fun createAccount(@Body acc: AccountDto): AccountDto

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @POST("sync/push")
    suspend fun pushChanges(@Body body: SyncPushRequest)

    @GET("sync/pull")
    suspend fun pullChanges(): SyncPullResponse
}
