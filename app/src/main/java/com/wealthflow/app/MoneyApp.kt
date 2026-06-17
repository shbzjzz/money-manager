package com.wealthflow.app

import android.app.Application
import com.wealthflow.app.data.repository.FinanceRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MoneyApp : Application() {

    @Inject lateinit var repository: FinanceRepository

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            repository.seedDefaultCategoriesIfEmpty()
            repository.seedDefaultAccountIfEmpty()
        }
    }
}
