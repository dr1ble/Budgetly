package shmr.budgetly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import shmr.budgetly.ui.navigation.RootNavGraph
import shmr.budgetly.ui.screens.splash.SplashViewModel
import shmr.budgetly.ui.theme.BudgetlyTheme

/**
 * Главная и единственная Activity в приложении.
 * Отвечает за настройку окна, установку SplashScreen и отображение основного контента
 * с помощью Jetpack Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setupSplashScreen(splashScreen)
        setupWindow()

        setContent {
            BudgetlyTheme(darkTheme = false) {
                RootNavGraph()
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