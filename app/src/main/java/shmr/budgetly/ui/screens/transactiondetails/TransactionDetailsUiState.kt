package shmr.budgetly.ui.screens.transactiondetails

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.ui.navigation.Expenses
import java.time.LocalDate
import java.time.LocalTime

data class TransactionDetailsUiState(
    // Режим экрана
    val isEditMode: Boolean = false,
    val isIncome: Boolean = false,

    // Состояние загрузки/ошибки
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: DomainError? = null,
    val saveError: DomainError? = null,
    val isSaveSuccess: Boolean = false,

    // Данные полей
    val amount: String = "",
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val comment: String = "",

    // Данные для выбора
    val availableAccounts: List<Account> = emptyList(),
    val availableCategories: List<Category> = emptyList(),

    // Состояние UI-компонентов
    val isDatePickerVisible: Boolean = false,
    val isTimePickerVisible: Boolean = false,
    val isAccountPickerVisible: Boolean = false,
    val isCategoryPickerVisible: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,

    // Навигационные данные
    val parentRoute: String = Expenses::class.qualifiedName!!
) {
    /**
     * Вычисляемое свойство, определяющее, активна ли кнопка сохранения.
     */
    val isSaveEnabled: Boolean
        get() = amount.isNotBlank() &&
                (amount.replace(',', '.').toDoubleOrNull() ?: 0.0) > 0.0 &&
                selectedAccount != null &&
                selectedCategory != null &&
                !isLoading &&
                !isSaving
}