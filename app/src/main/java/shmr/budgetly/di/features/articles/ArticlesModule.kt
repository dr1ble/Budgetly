package shmr.budgetly.di.features.articles

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.articles.ArticlesViewModel

@Module
abstract class ArticlesModule {
    @Binds
    @IntoMap
    @ViewModelKey(ArticlesViewModel::class)
    abstract fun bindArticlesViewModel(viewModel: ArticlesViewModel): ViewModel
}