package shmr.budgetly.ui.screens.articles

import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.util.DomainError

data class ArticlesUiState(
    val isLoading: Boolean = false,
    val error: DomainError? = null,
    val searchQuery: String = "",
    private val allCategories: List<Category> = emptyList()
) {
    /**
     * Вычисляемый список отфильтрованных категорий для отображения в UI.
     * Фильтрация происходит по [searchQuery] без учета регистра.
     */
    val filteredCategories: List<Category>
        get() = if (searchQuery.isBlank()) {
            allCategories
        } else {
            allCategories.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
}