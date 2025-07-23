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

private val LightColorScheme = lightColorScheme(
    primary = Green400,
    onPrimary = Black900,
    secondary = Green200,
    onSecondary = Black900,
    background = White100,
    onBackground = Black900,
    surface = White200,
    onSurface = Black900,
    surfaceVariant = Grey200,
    onSurfaceVariant = Black700,
    secondaryContainer = Green200,
    onSecondaryContainer = Green400,
    outline = Grey400,
    outlineVariant = Grey200,
    error = Red400,
    errorContainer = Red400,
    onErrorContainer = White100
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSurface,
    onSecondary = DarkOnSurface,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkBackgroundBar,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = DarkOnSurface,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = DarkError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnError
)


/**
 * Основная тема приложения Budgetly.
 * Применяет цветовые схемы, типографику, формы и настраивает системные бары.
 *
 * @param darkTheme Использовать ли темную тему. По умолчанию определяется системой.
 * @param dynamicColor Использовать ли динамические цвета (Material You). Доступно на Android 12+.
 * @param content Контент, к которому применяется тема.
 */
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

    CompositionLocalProvider(LocalDimens provides defaultDimens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}