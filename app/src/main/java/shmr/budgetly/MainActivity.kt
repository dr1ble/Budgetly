package shmr.budgetly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import shmr.budgetly.ui.navigation.RootNavGraph
import shmr.budgetly.ui.screens.splash.SplashViewModel
import shmr.budgetly.ui.theme.BudgetlyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !viewModel.isReady.value }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BudgetlyTheme {
                RootNavGraph()
            }
        }
    }
}
