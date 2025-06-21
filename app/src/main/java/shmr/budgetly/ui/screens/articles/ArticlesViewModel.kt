package shmr.budgetly.ui.screens.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.repository.BudgetlyRepository
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

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val repository: BudgetlyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories(isInitialLoad = true)
    }

    fun loadCategories(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = isInitialLoad, error = null) }

            when (val result = repository.getAllCategories()) {
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            allCategories = result.data,
                            filteredCategories = filterCategories(
                                result.data,
                                currentState.searchQuery
                            )
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredCategories = filterCategories(currentState.allCategories, query)
            )
        }
    }

    private fun filterCategories(categories: List<Category>, query: String): List<Category> {
        return if (query.isBlank()) {
            categories
        } else {
            categories.filter { category ->
                category.name.contains(query, ignoreCase = true)
            }
        }
    }
}