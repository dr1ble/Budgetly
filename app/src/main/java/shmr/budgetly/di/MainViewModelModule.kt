package shmr.budgetly.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.domain.monitor.HapticFeedbackManager
import shmr.budgetly.domain.repository.UserPreferencesRepository
import shmr.budgetly.domain.usecase.GetHapticSettingsUseCase
import shmr.budgetly.domain.usecase.GetThemeColorUseCase
import shmr.budgetly.ui.screens.main.MainViewModel

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
            getThemeColorUseCase: GetThemeColorUseCase,
            getHapticSettingsUseCase: GetHapticSettingsUseCase,
            hapticFeedbackManager: HapticFeedbackManager
        ): MainViewModel {
            return MainViewModel(
                userPreferencesRepository,
                getThemeColorUseCase,
                getHapticSettingsUseCase,
                hapticFeedbackManager
            )
        }
    }
}