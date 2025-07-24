package shmr.budgetly.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import shmr.budgetly.BudgetlyApp
import shmr.budgetly.ui.di.rememberScreenComponent
import shmr.budgetly.ui.screens.main.MainScreen
import shmr.budgetly.ui.screens.settings.pincode.PinCodeScreen
import shmr.budgetly.ui.screens.splash.SplashScreen
import shmr.budgetly.ui.screens.splash.SplashViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun RootNavGraph() {
    val appComponent = (LocalContext.current.applicationContext as BudgetlyApp).appComponent
    val splashViewModel: SplashViewModel = viewModel(factory = appComponent.viewModelFactory())
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> {
            val uiState by splashViewModel.uiState.collectAsStateWithLifecycle()

            // Переходим, когда ViewModel разрешит
            if (uiState.isNavigationAllowed) {
                LaunchedEffect(Unit) {
                    uiState.navigationDestination?.let { destination ->
                        navController.navigate(destination) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    }
                }
            }

            // Показываем сплэш, пока навигация не разрешена
            if (!uiState.isNavigationAllowed) {
                SplashScreen(
                    onAnimationFinished = splashViewModel::onAnimationFinished
                )
            }
        }

        composable<Pin> { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.pinCodeComponent().create() }
            PinCodeScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                onPinVerified = {
                    navController.navigate(Main) { popUpTo<Pin> { inclusive = true } }
                },
                onPinCreated = {
                    navController.navigate(Pin(PinScreenPurpose.UNLOCK)) {
                        popUpTo<Pin> {
                            inclusive = true
                        }
                    }
                },
                onPinCleared = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable<Main> {
            MainScreen(
                onNavigateToPin = { purpose ->
                    navController.navigate(Pin(purpose))
                }
            )
        }
    }
}