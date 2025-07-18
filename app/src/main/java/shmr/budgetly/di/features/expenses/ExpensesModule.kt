package shmr.budgetly.di.features.expenses

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.expenses.ExpensesViewModel

@Module
abstract class ExpensesModule {
    @Binds
    @IntoMap
    @ViewModelKey(ExpensesViewModel::class)
    abstract fun bindExpensesViewModel(viewModel: ExpensesViewModel): ViewModel
}