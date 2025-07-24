package shmr.budgetly.di.features.about

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.settings.about.AboutAppViewModel

@Module
abstract class AboutAppModule {
    @Binds
    @IntoMap
    @ViewModelKey(AboutAppViewModel::class)
    abstract fun bindAboutAppViewModel(viewModel: AboutAppViewModel): ViewModel
}