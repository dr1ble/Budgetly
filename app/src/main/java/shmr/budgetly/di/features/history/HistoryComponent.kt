package shmr.budgetly.di.features.history

import dagger.Subcomponent
import shmr.budgetly.di.scope.ScreenScope
import shmr.budgetly.di.viewmodel.ViewModelFactoryModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider

@ScreenScope
@Subcomponent(modules = [HistoryModule::class, ViewModelFactoryModule::class])
interface HistoryComponent : ViewModelFactoryProvider {

    @Subcomponent.Factory
    interface Factory {
        fun create(): HistoryComponent
    }
}