package shmr.budgetly.di.features.analyze

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.AssistedSavedStateViewModelFactory
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.analyze.AnalyzeViewModel

@Module
abstract class AnalyzeModule {
    @Binds
    @IntoMap
    @ViewModelKey(AnalyzeViewModel::class)
    abstract fun bindAnalyzeViewModelFactory(
        factory: AnalyzeViewModel.Factory
    ): AssistedSavedStateViewModelFactory<out ViewModel>
}