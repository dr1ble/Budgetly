package shmr.budgetly.di.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * Обобщенная фабрика для создания ViewModel, которым требуется SavedStateHandle.
 * Dagger предоставляет `creator`, а `CreationExtras` предоставляет `SavedStateHandle`.
 *
 * @param creator Лямбда, которая принимает SavedStateHandle и возвращает экземпляр ViewModel.
 */
class AssistedViewModelFactory<T : ViewModel>(
    private val creator: (handle: SavedStateHandle) -> T
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        try {
            @Suppress("UNCHECKED_CAST")
            return creator(extras.createSavedStateHandle()) as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}