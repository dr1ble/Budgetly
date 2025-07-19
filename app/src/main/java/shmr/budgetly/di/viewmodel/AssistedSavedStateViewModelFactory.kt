package shmr.budgetly.di.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Общий интерфейс для всех фабрик, которые создают ViewModel с помощью SavedStateHandle.
 * Dagger будет предоставлять реализации этого интерфейса.
 *
 * @param T Тип ViewModel, который будет создан.
 */
interface AssistedSavedStateViewModelFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}