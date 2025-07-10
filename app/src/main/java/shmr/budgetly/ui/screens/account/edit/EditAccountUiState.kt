package shmr.budgetly.ui.screens.account.edit

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
        get() = !isLoading && initialAccount != null && name != null && balance != null && currency != null && (
                name != initialAccount.name ||
                        balance != initialAccount.balance ||
                        currency != initialAccount.currency
                )
}