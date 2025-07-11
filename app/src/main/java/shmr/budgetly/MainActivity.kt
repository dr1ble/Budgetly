package shmr.budgetly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import shmr.budgetly.di.MainComponent
import shmr.budgetly.ui.di.LocalViewModelFactory
import shmr.budgetly.ui.navigation.RootNavGraph
import shmr.budgetly.ui.screens.splash.SplashViewModel
import shmr.budgetly.ui.theme.BudgetlyTheme

/**
 * Главная и единственная Activity в приложении.
 * Отвечает за настройку окна, установку SplashScreen и отображение основного контента
 * с помощью Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    lateinit var mainComponent: MainComponent
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent = (application as BudgetlyApp).appComponent.mainComponent().create()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setupSplashScreen(splashScreen)
        setupWindow()

        setContent {
            BudgetlyTheme(darkTheme = false) {
                // Предоставляем фабрику ViewModel в виде CompositionLocal
                // чтобы все дочерние @Composable функции могли её использовать
                CompositionLocalProvider(
                    LocalViewModelFactory provides mainComponent.viewModelFactory()
                ) {
                    RootNavGraph()
                }
            }
        }
    }

    /**
     * Настраивает поведение SplashScreen.
     */
    private fun setupSplashScreen(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition { !viewModel.isReady.value }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }
    }

    /**
     * Настраивает окно для отображения в режиме "edge-to-edge".
     */
    private fun setupWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}