package shmr.budgetly

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shmr.budgetly.ui.navigation.RootNavGraph
import shmr.budgetly.ui.screens.main.MainViewModel
import shmr.budgetly.ui.screens.splash.SplashViewModel
import shmr.budgetly.ui.theme.BudgetlyTheme

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels {
        (application as BudgetlyApp).appComponent.viewModelFactory()
    }

    private val mainViewModel: MainViewModel by viewModels {
        (application as BudgetlyApp).appComponent.viewModelFactory()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setupSplashScreen(splashScreen)
        setupWindow()

        setContent {
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val themeColor by mainViewModel.themeColor.collectAsStateWithLifecycle()

            BudgetlyTheme(
                darkTheme = isDarkTheme,
                themeColor = themeColor
            ) {
                RootNavGraph()
            }
        }
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition {
            !splashViewModel.uiState.value.isReadyToNavigate
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }
    }

    private fun setupWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}