package shmr.budgetly.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.ui.screens.account.AccountViewModel
import shmr.budgetly.ui.screens.account.edit.EditAccountViewModel
import shmr.budgetly.ui.screens.articles.ArticlesViewModel
import shmr.budgetly.ui.screens.expenses.ExpensesViewModel
import shmr.budgetly.ui.screens.history.HistoryViewModel
import shmr.budgetly.ui.screens.incomes.IncomesViewModel
import shmr.budgetly.ui.screens.settings.SettingsViewModel

/**
 * Модуль Dagger, отвечающий за предоставление всех ViewModel в приложении.
 * Он связывает каждую ViewModel с картой провайдеров, которая затем
 * используется в ViewModelFactory для создания экземпляров ViewModel.
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditAccountViewModel::class)
    abstract fun bindEditAccountViewModel(viewModel: EditAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticlesViewModel::class)
    abstract fun bindArticlesViewModel(viewModel: ArticlesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ExpensesViewModel::class)
    abstract fun bindExpensesViewModel(viewModel: ExpensesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModel(viewModel: HistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IncomesViewModel::class)
    abstract fun bindIncomesViewModel(viewModel: IncomesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}