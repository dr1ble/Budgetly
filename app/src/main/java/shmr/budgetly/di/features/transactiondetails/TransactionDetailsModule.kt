package shmr.budgetly.di.features.transactiondetails

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.transactiondetails.TransactionDetailsViewModel

@Module
abstract class TransactionDetailsModule {
    @Binds
    @IntoMap
    @ViewModelKey(TransactionDetailsViewModel::class)
    abstract fun bindTransactionDetailsViewModel(viewModel: TransactionDetailsViewModel): ViewModel
}