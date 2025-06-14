package shmr.budgetly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import shmr.budgetly.ui.screens.MainScreen
import shmr.budgetly.ui.theme.BudgetlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BudgetlyTheme {
                MainScreen()
            }
        }
    }
}
