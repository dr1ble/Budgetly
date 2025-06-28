package shmr.budgetly.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.usecase.GetAllCategoriesUseCase
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

data class ArticlesUiState(
    val searchQuery: String = "",
    val allCategories: List<Category> = emptyList(),
    val filteredCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: DomainError? = null
)

/**
 * ViewModel для экрана "Статьи" (Категории).
 * Отвечает за:
 * 1. Загрузку списка всех категорий через [GetAllCategoriesUseCase].
 * 2. Фильтрацию списка категорий на основе поискового запроса пользователя.
 * 3. Управление состоянием UI ([ArticlesUiState]).
 */
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val getAllCategories: GetAllCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories(isInitialLoad = true)
    }

    /**
     * Инициирует загрузку списка категорий.
     */
    fun loadCategories(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = isInitialLoad, error = null) }
            processResult(getAllCategories())
        }
    }

    /**
     * Обрабатывает результат загрузки категорий.
     */
    private fun processResult(result: Result<List<Category>>) {
        when (result) {
            is Result.Success -> _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    allCategories = result.data,
                    filteredCategories = filterCategories(result.data, currentState.searchQuery)
                )
            }

            is Result.Error -> _uiState.update {
                it.copy(isLoading = false, error = result.error)
            }
        }
    }

    /**
     * Обновляет состояние при изменении поискового запроса.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredCategories = filterCategories(currentState.allCategories, query)
            )
        }
    }

    /**
     * Фильтрует категории по имени на основе поискового запроса.
     */
    private fun filterCategories(categories: List<Category>, query: String): List<Category> {
        return if (query.isBlank()) {
            categories
        } else {
            categories.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}