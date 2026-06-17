package com.wealthflow.app.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthflow.app.data.local.entity.AccountEntity
import com.wealthflow.app.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountsUiState(
    val accounts: List<AccountEntity> = emptyList(),
    val netWorth: Double = 0.0,
    val liabilities: Double = 0.0
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    val uiState = combine(
        repository.observeAccounts(),
        repository.observeNetAssets(),
        repository.observeLiabilities()
    ) { accounts, net, liabilities ->
        AccountsUiState(accounts, net, liabilities)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccountsUiState())

    fun addAccount(account: AccountEntity) {
        viewModelScope.launch { repository.upsertAccount(account) }
    }
}
