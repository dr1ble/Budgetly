package shmr.budgetly.ui.screens.account.edit

import android.util.Log
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.util.DomainError

data class EditAccountUiState(
    val isInitialLoading: Boolean = true,
    val isLoading: Boolean = false,
    val error: DomainError? = null,
    val isSaveSuccess: Boolean = false,
    val name: String? = null,
    val balance: String? = null,
    val currency: String? = null,
    val isBottomSheetVisible: Boolean = false,
    private val initialAccount: Account? = null
) {
    /**
     * Вычисляемое свойство, определяющее, активна ли кнопка сохранения.
     * Кнопка активна, если данные загружены, не идет процесс сохранения
     * и хотя бы одно из полей было изменено.
     */
    val isSaveEnabled: Boolean
        get() {
            if (isLoading || initialAccount == null || name == null || balance == null || currency == null) {
                return false
            }

            // ОЧИЩАЕМ ОБА ЗНАЧЕНИЯ ПЕРЕД СРАВНЕНИЕМ
            val initialBalanceCleaned = initialAccount.balance.filter { it.isDigit() || it == '.' }
            val currentBalanceCleaned = balance.filter { it.isDigit() || it == '.' }

            val initialBalanceValue = initialBalanceCleaned.toDoubleOrNull()
            val currentBalanceValue = currentBalanceCleaned.toDoubleOrNull()

            val isNameChanged = name != initialAccount.name
            val isBalanceChanged = initialBalanceValue != currentBalanceValue
            val isCurrencyChanged = currency != initialAccount.currency

            Log.d("EditAccountDebug", "--- Checking isSaveEnabled ---")
            Log.d(
                "EditAccountDebug",
                "Initial: name='${initialAccount.name}', balance='${initialAccount.balance}' -> '$initialBalanceCleaned' ($initialBalanceValue), currency='${initialAccount.currency}'"
            )
            Log.d(
                "EditAccountDebug",
                "Current: name='$name', balance='$balance' -> '$currentBalanceCleaned' ($currentBalanceValue), currency='$currency'"
            )
            Log.d(
                "EditAccountDebug",
                "Changes: name=$isNameChanged, balance=$isBalanceChanged, currency=$isCurrencyChanged"
            )
            Log.d(
                "EditAccountDebug",
                "Result: ${isNameChanged || isBalanceChanged || isCurrencyChanged}"
            )
            Log.d("EditAccountDebug", "---------------------------------")


            return isNameChanged || isBalanceChanged || isCurrencyChanged
        }
}