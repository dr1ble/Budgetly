package shmr.budgetly.di.features.editaccount

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.account.edit.EditAccountViewModel

@Module
abstract class EditAccountModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditAccountViewModel::class)
    abstract fun bindEditAccountViewModel(viewModel: EditAccountViewModel): ViewModel
}