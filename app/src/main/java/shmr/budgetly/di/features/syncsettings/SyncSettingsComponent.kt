package shmr.budgetly.di.features.syncsettings

import dagger.Subcomponent
import shmr.budgetly.di.scope.ScreenScope
import shmr.budgetly.di.viewmodel.ViewModelFactoryModule
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider

@ScreenScope
@Subcomponent(modules = [SyncSettingsModule::class, ViewModelFactoryModule::class])
interface SyncSettingsComponent : ViewModelFactoryProvider {

    @Subcomponent.Factory
    interface Factory {
        fun create(): SyncSettingsComponent
    }
}