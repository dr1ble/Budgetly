package shmr.budgetly.ui.screens.articles

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.repository.BudgetlyRepository
import javax.inject.Inject

data class ArticlesUiState(
    val searchQuery: String = "",
    val allCategories: List<Category> = emptyList(),
    val filteredCategories: List<Category> = emptyList()
)

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val repository: BudgetlyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val categories = repository.getAllCategories()
        _uiState.value = ArticlesUiState(
            allCategories = categories,
            filteredCategories = categories
        )
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            val filteredList = if (query.isBlank()) {
                currentState.allCategories
            } else {
                currentState.allCategories.filter { category ->
                    category.name.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                filteredCategories = filteredList
            )
        }
    }
}