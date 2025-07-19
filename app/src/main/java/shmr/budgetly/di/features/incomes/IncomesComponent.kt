package shmr.budgetly.di.features.incomes

import dagger.Subcomponent
import shmr.budgetly.di.scope.ScreenScope
import shmr.budgetly.di.viewmodel.ViewModelFactoryModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider

@ScreenScope
@Subcomponent(modules = [IncomesModule::class, ViewModelFactoryModule::class])
interface IncomesComponent : ViewModelFactoryProvider {

    @Subcomponent.Factory
    interface Factory {
        fun create(): IncomesComponent
    }
}