package shmr.budgetly.di.features.syncsettings

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.settings.sync.SyncSettingsViewModel

@Module
abstract class SyncSettingsModule {
    @Binds
    @IntoMap
    @ViewModelKey(SyncSettingsViewModel::class)
    abstract fun bindSyncSettingsViewModel(viewModel: SyncSettingsViewModel): ViewModel
}