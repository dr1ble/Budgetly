package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import shmr.budgetly.ui.screens.MainScreen
import shmr.budgetly.ui.screens.splash.SplashScreen
import shmr.budgetly.ui.screens.splash.SplashViewModel

@Composable
fun RootNavGraph() {
    val splashViewModel: SplashViewModel = viewModel()

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreen(
                onReady = { splashViewModel.setReady() },
                onAnimationFinished = {
                    navController.navigate(Main) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<Main> {
            MainScreen()
        }
    }
}