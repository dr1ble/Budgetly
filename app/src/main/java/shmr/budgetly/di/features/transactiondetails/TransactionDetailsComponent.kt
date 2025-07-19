package shmr.budgetly.di.features.transactiondetails

import dagger.Subcomponent
import shmr.budgetly.di.scope.ScreenScope
import shmr.budgetly.di.viewmodel.ViewModelFactoryModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider

@ScreenScope
@Subcomponent(modules = [TransactionDetailsModule::class, ViewModelFactoryModule::class])
interface TransactionDetailsComponent : ViewModelFactoryProvider {

    @Subcomponent.Factory
    interface Factory {
        fun create(): TransactionDetailsComponent
    }
}