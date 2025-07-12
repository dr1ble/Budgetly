package shmr.budgetly.di

import androidx.lifecycle.ViewModelProvider
import dagger.Subcomponent
import shmr.budgetly.MainActivity
import shmr.budgetly.di.viewmodel.ViewModelModule

/**
 * Сабкомпонент Dagger, связанный с жизненным циклом MainActivity.
 * Предоставляет зависимости, необходимые для Activity и ее дочерних экранов,
 * в первую очередь - фабрику для ViewModel.
 */
@Subcomponent(modules = [ViewModelModule::class])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(activity: MainActivity)

    fun viewModelFactory(): ViewModelProvider.Factory
}