package shmr.budgetly.di.features.pincode

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.AssistedSavedStateViewModelFactory
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.settings.pincode.PinCodeViewModel

@Module
abstract class PinCodeModule {
    @Binds
    @IntoMap
    @ViewModelKey(PinCodeViewModel::class)
    abstract fun bindPinCodeViewModelFactory(
        factory: PinCodeViewModel.Factory
    ): AssistedSavedStateViewModelFactory<out ViewModel>
}