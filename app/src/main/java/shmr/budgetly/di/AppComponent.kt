package shmr.budgetly.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import shmr.budgetly.di.features.account.AccountComponent
import shmr.budgetly.di.features.articles.ArticlesComponent
import shmr.budgetly.di.features.editaccount.EditAccountComponent
import shmr.budgetly.di.features.expenses.ExpensesComponent
import shmr.budgetly.di.features.history.HistoryComponent
import shmr.budgetly.di.features.incomes.IncomesComponent
import shmr.budgetly.di.features.settings.SettingsComponent
import shmr.budgetly.di.features.transactiondetails.TransactionDetailsComponent
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.di.viewmodel.AssistedViewModelModule
import shmr.budgetly.di.viewmodel.ViewModelModule

/**
 * Корневой компонент Dagger для всего приложения.
 * Он живет в течение всего жизненного цикла приложения и предоставляет
 * глобальные зависимости (синглтоны), такие как сетевой клиент, репозитории и т.д.
 * Также он является фабрикой для всех экранных сабкомпонентов.
 */
@AppScope
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DataModule::class,
        ViewModelModule::class,
        AssistedViewModelModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun expensesComponent(): ExpensesComponent.Factory
    fun incomesComponent(): IncomesComponent.Factory
    fun accountComponent(): AccountComponent.Factory
    fun articlesComponent(): ArticlesComponent.Factory
    fun settingsComponent(): SettingsComponent.Factory
    fun historyComponent(): HistoryComponent.Factory
    fun editAccountComponent(): EditAccountComponent.Factory
    fun transactionDetailsComponent(): TransactionDetailsComponent.Factory
}