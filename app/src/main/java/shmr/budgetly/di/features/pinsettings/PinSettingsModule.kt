package shmr.budgetly.di.features.pinsettings

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.pincode.PinSettingsViewModel

@Module
abstract class PinSettingsModule {
    @Binds
    @IntoMap
    @ViewModelKey(PinSettingsViewModel::class)
    abstract fun bindPinSettingsViewModel(viewModel: PinSettingsViewModel): ViewModel
}