package com.wealthflow.app.presentation.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthflow.app.data.local.entity.CategoryEntity
import com.wealthflow.app.data.local.entity.CategoryKind
import com.wealthflow.app.data.local.entity.AccountEntity
import com.wealthflow.app.data.local.entity.TransactionEntity
import com.wealthflow.app.data.local.entity.TxType
import com.wealthflow.app.data.repository.FinanceRepository
import com.wealthflow.app.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionFormState(
    val type: TxType = TxType.EXPENSE,
    val amountText: String = "",
    val selectedCategory: CategoryEntity? = null,
    val selectedAccount: AccountEntity? = null,
    val note: String = "",
    val categories: List<CategoryEntity> = emptyList(),
    val accounts: List<AccountEntity> = emptyList(),
    val saved: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val addTransaction: AddTransactionUseCase
) : ViewModel() {

    private val type = MutableStateFlow(TxType.EXPENSE)
    private val amountText = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<CategoryEntity?>(null)
    private val selectedAccount = MutableStateFlow<AccountEntity?>(null)
    private val note = MutableStateFlow("")

    private data class FormInputs(
        val type: TxType,
        val amountText: String,
        val selectedCategory: CategoryEntity?,
        val selectedAccount: AccountEntity?,
        val note: String
    )

    private val inputs = combine(type, amountText, selectedCategory, selectedAccount, note) {
        t, amt, cat, acc, n -> FormInputs(t, amt, cat, acc, n)
    }

    val state: StateFlow<AddTransactionFormState> = combine(
        inputs, repository.observeAllCategories(), repository.observeAccounts()
    ) { form, categories, accounts ->
        AddTransactionFormState(
            type = form.type,
            amountText = form.amountText,
            selectedCategory = form.selectedCategory,
            selectedAccount = form.selectedAccount,
            note = form.note,
            categories = categories,
            accounts = accounts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddTransactionFormState())

    fun onTypeChange(t: TxType) { type.value = t }
    fun onAmountChange(v: String) { amountText.value = v }
    fun onCategorySelect(c: CategoryEntity) { selectedCategory.value = c }
    fun onAccountSelect(a: AccountEntity) { selectedAccount.value = a }
    fun onNoteChange(v: String) { note.value = v }

    fun save(onDone: () -> Unit) {
        val amount = amountText.value.toDoubleOrNull() ?: return
        val account = selectedAccount.value ?: return
        viewModelScope.launch {
            addTransaction(
                TransactionEntity(
                    amount = amount,
                    type = type.value,
                    categoryId = selectedCategory.value?.id,
                    accountId = account.id,
                    date = System.currentTimeMillis(),
                    note = note.value
                )
            )
            onDone()
        }
    }
}
