package shmr.budgetly.ui.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import shmr.budgetly.BudgetlyApp
import shmr.budgetly.di.AppComponent
import shmr.budgetly.di.viewmodel.ViewModelFactoryProvider

/**
 * ViewModel, используемая исключительно как "держатель" для Dagger-компонента,
 * чтобы привязать его жизненный цикл к NavBackStackEntry.
 */
class ComponentHolderViewModel<C> : ViewModel() {
    var component: C? = null
}

/**
 * Composable-хелпер для создания и запоминания Dagger-компонента,
 * который будет жить пока экран находится в back stack.
 *
 * @param navBackStackEntry Запись из back stack, к которой будет привязан жизненный цикл.
 * @param componentFactory Лямбда для создания экземпляра компонента из AppComponent.
 * @return Экземпляр экранного Dagger-компонента.
 */
@Composable
inline fun <reified C : ViewModelFactoryProvider> rememberScreenComponent(
    navBackStackEntry: NavBackStackEntry,
    crossinline componentFactory: (AppComponent) -> C
): C {
    val appComponent =
        (LocalContext.current.applicationContext as BudgetlyApp).appComponent

    val holder: ComponentHolderViewModel<C> = viewModel(viewModelStoreOwner = navBackStackEntry)

    return remember(navBackStackEntry) {
        if (holder.component == null) {
            holder.component = componentFactory(appComponent)
        }
        holder.component!!
    }
}