package shmr.budgetly.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import shmr.budgetly.BudgetlyApp
import shmr.budgetly.di.features.account.AccountComponent
import shmr.budgetly.di.features.analyze.AnalyzeComponent
import shmr.budgetly.di.features.articles.ArticlesComponent
import shmr.budgetly.di.features.colorpicker.ColorPickerComponent
import shmr.budgetly.di.features.editaccount.EditAccountComponent
import shmr.budgetly.di.features.expenses.ExpensesComponent
import shmr.budgetly.di.features.haptics.HapticsComponent
import shmr.budgetly.di.features.history.HistoryComponent
import shmr.budgetly.di.features.incomes.IncomesComponent
import shmr.budgetly.di.features.settings.SettingsComponent
import shmr.budgetly.di.features.transactiondetails.TransactionDetailsComponent
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.di.viewmodel.AssistedViewModelModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider
import shmr.budgetly.di.viewmodel.ViewModelModule
import shmr.budgetly.work.DaggerWorkerFactory

@AppScope
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DataModule::class,
        DatabaseModule::class,
        ViewModelModule::class,
        AssistedViewModelModule::class,
        ViewModelFactoryModule::class, // Добавляем модуль фабрики
        MainViewModelModule::class     // Добавляем модуль для MainViewModel
    ]
)
interface AppComponent : ViewModelFactoryProvider { // Реализуем интерфейс провайдера

    fun inject(app: BudgetlyApp)

    fun workerFactory(): DaggerWorkerFactory

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

    fun analyzeComponent(): AnalyzeComponent.Factory

    fun colorPickerComponent(): ColorPickerComponent.Factory
    fun hapticsComponent(): HapticsComponent.Factory
}