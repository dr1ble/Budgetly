package shmr.budgetly.di.features.incomes

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.incomes.IncomesViewModel

@Module
abstract class IncomesModule {
    @Binds
    @IntoMap
    @ViewModelKey(IncomesViewModel::class)
    abstract fun bindIncomesViewModel(viewModel: IncomesViewModel): ViewModel
}