package shmr.budgetly.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

/**
 * A helper Composable function to manage the color and appearance of the system bars.
 * This function implements the Edge-to-Edge display and ensures graceful degradation
 * for older Android versions.
 *
 * @param colorScheme The current color scheme to retrieve the correct background colors.
 * @param darkTheme A flag indicating whether the dark theme is currently in use.
 */
@Composable
internal fun ApplySystemBars(colorScheme: ColorScheme, darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowInsetsControllerCompat(window, view)

            // --- Status Bar (Top) ---
            insetsController.isAppearanceLightStatusBars = !darkTheme

            window.statusBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // On API 29+, the background is transparent for a true Edge-to-Edge experience.
                Color.Transparent.toArgb()
            } else {
                // On API < 29, the background matches the TopAppBar color for a seamless look.
                colorScheme.primary.toArgb()
            }

            // --- Navigation Bar (Bottom) ---
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // API 29+: Full transparency and contrast control.
                window.isNavigationBarContrastEnforced = false
                window.navigationBarColor = Color.Transparent.toArgb()
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                // API 27-28: Background matches the app's background; icons can be dark.
                window.navigationBarColor = colorScheme.background.toArgb()
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            } else {
                // API 26 and below: Cannot make icons dark, so we leave the background black
                // to ensure the light icons are readable.
                window.navigationBarColor = Color.Black.toArgb()
            }
        }
    }
}