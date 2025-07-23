package shmr.budgetly.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import shmr.budgetly.domain.repository.UserPreferencesRepository
import shmr.budgetly.domain.usecase.GetThemeColorUseCase
import shmr.budgetly.ui.screens.main.MainViewModel
import shmr.budgetly.di.viewmodel.ViewModelKey

@Module
abstract class MainViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    companion object {
        @Provides
        fun provideMainViewModel(
            userPreferencesRepository: UserPreferencesRepository,
            getThemeColorUseCase: GetThemeColorUseCase
        ): MainViewModel {
            return MainViewModel(userPreferencesRepository, getThemeColorUseCase)
        }
    }
}