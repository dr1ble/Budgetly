package shmr.budgetly.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green400,
    secondary = Green200,
    tertiary = White300,
    secondaryContainer = Green200,
    onSecondaryContainer = Green400,
    background = White100,
    surface = White200,
    surfaceContainer = White200,
    surfaceVariant = Grey200,
    onPrimary = Black900,
    onSecondary = Black900,
    onBackground = Black900,
    onSurface = Black900,
    onSurfaceVariant = Black700,
    outline = Grey600,
    outlineVariant = Grey400
)

private val LightColorScheme = lightColorScheme(
    primary = Green400,
    secondary = Green200,
    tertiary = White300,
    secondaryContainer = Green200,
    onSecondaryContainer = Green400,
    background = White100,
    surface = White200,
    surfaceContainer = White200,
    surfaceVariant = Grey200,
    onPrimary = Black900,
    onSecondary = Black900,
    onBackground = Black900,
    onSurface = Black900,
    onSurfaceVariant = Black700,
    outline = Grey600,
    outlineVariant = Grey400
)

@Composable
fun BudgetlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    ApplySystemBars(colorScheme = colorScheme, darkTheme = darkTheme)

    val dimens = defaultDimens

    CompositionLocalProvider(LocalDimens provides dimens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}