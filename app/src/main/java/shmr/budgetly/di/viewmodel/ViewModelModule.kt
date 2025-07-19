package shmr.budgetly.di.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * Dagger-модуль, который объявляет мультибиндинг для карты простых ViewModel.
 * Это позволяет Dagger внедрять Map<Class, Provider<ViewModel>>,
 * даже если в конкретном компоненте нет биндингов для ViewModel.
 */
@Module
abstract class ViewModelModule {
    @Multibinds
    abstract fun viewModels(): Map<Class<out ViewModel>, ViewModel>
}