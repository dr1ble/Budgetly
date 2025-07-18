package shmr.budgetly.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.usecase.GetAllCategoriesUseCase
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

/**
 * ViewModel для экрана "Статьи".
 * Отвечает за:
 * 1. Загрузку списка всех категорий, которые в данном контексте выступают как "статьи".
 * 2. Обработку поискового запроса от пользователя.
 * 3. Управление состоянием UI ([ArticlesUiState]).
 */

class ArticlesViewModel @Inject constructor(
    private val getAllCategories: GetAllCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Публичный метод для повторной попытки загрузки данных с UI.
     */
    fun onRetry() {
        loadCategories()
    }

    /**
     * Инициирует загрузку категорий.
     * @param isInitialLoad true для первоначальной загрузки, false для фонового обновления.
     */
    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getAllCategories().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                allCategories = result.data
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.error)
                        }
                    }
                }
            }
        }
    }


    /**
     * Обрабатывает изменение текста в поисковой строке.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}