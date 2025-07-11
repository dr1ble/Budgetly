package shmr.budgetly.ui.di

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider

/**
 * CompositionLocal для предоставления ViewModelProvider.Factory в дереве Composable.
 * Это позволяет получать ViewModel через hiltViewModel-подобный синтаксис
 * без явной передачи фабрики в каждую Composable функцию.
 */
val LocalViewModelFactory = compositionLocalOf<ViewModelProvider.Factory> {
    error("No ViewModelFactory provided")
}