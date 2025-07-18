package shmr.budgetly.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import javax.inject.Inject
import javax.inject.Provider

/**
 * Универсальная фабрика для создания всех ViewModel в приложении.
 * Dagger внедряет сюда две карты:
 * 1. `creators` - для ViewModel с простыми зависимостями.
 * 2. `assistedCreators` - для ViewModel, которым требуется SavedStateHandle.
 */
class ViewModelFactory @Inject constructor(
    private val creators: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>,
    private val assistedCreators: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<AssistedSavedStateViewModelFactory<out ViewModel>>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Сначала ищем в карте "ассистируемых" фабрик
        val assistedFactoryProvider = assistedCreators[modelClass]
            ?: assistedCreators.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value

        if (assistedFactoryProvider != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                return assistedFactoryProvider.get().create(extras.createSavedStateHandle()) as T
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        // Если не нашли, ищем в карте обычных фабрик
        val creatorProvider = creators[modelClass]
            ?: creators.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: throw IllegalArgumentException("unknown model class $modelClass")

        try {
            @Suppress("UNCHECKED_CAST")
            return creatorProvider.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}