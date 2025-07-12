package shmr.budgetly.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal для предоставления лямбды, которая позволяет
 * дочерним экранам устанавливать свой TopAppBar на родительском Scaffold.
 */
val LocalTopAppBarSetter = compositionLocalOf<(@Composable () -> Unit) -> Unit> {
    error("No TopAppBar setter provided")
}