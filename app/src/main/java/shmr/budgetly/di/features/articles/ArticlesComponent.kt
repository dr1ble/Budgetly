package shmr.budgetly.di.features.articles

import dagger.Subcomponent
import shmr.budgetly.di.scope.ScreenScope
import shmr.budgetly.di.viewmodel.ViewModelFactoryModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider

@ScreenScope
@Subcomponent(modules = [ArticlesModule::class, ViewModelFactoryModule::class])
interface ArticlesComponent : ViewModelFactoryProvider {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ArticlesComponent
    }
}