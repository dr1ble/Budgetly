package shmr.budgetly.di.features.haptics

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.settings.haptics.HapticsViewModel

@Module
abstract class HapticsModule {
    @Binds
    @IntoMap
    @ViewModelKey(HapticsViewModel::class)
    abstract fun bindHapticsViewModel(viewModel: HapticsViewModel): ViewModel
}