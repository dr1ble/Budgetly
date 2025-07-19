package shmr.budgetly.di.features.analyze

import dagger.Subcomponent
import shmr.budgetly.di.scope.ScreenScope
import shmr.budgetly.di.viewmodel.ViewModelFactoryModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider

@ScreenScope
@Subcomponent(modules = [AnalyzeModule::class, ViewModelFactoryModule::class])
interface AnalyzeComponent : ViewModelFactoryProvider {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AnalyzeComponent
    }
}