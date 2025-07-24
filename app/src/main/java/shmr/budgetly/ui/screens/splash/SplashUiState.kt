import shmr.budgetly.ui.navigation.NavDestination

data class SplashUiState(
    val isDataLoaded: Boolean = false,
    val isNavigationAllowed: Boolean = false,
    val navigationDestination: NavDestination? = null
)