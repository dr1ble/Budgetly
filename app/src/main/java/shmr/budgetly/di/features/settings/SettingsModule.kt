package shmr.budgetly.di.features.settings

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.settings.SettingsViewModel

@Module
abstract class SettingsModule {
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}