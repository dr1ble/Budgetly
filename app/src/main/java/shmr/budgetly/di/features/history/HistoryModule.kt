package shmr.budgetly.di.features.history

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.AssistedSavedStateViewModelFactory
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.history.HistoryViewModel

@Module
abstract class HistoryModule {
    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModelFactory(
        factory: HistoryViewModel.Factory
    ): AssistedSavedStateViewModelFactory<out ViewModel>
}