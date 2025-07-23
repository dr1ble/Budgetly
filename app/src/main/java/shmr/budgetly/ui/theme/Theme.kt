package shmr.budgetly.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import shmr.budgetly.domain.model.ThemeColor


private val LightGreenColorScheme = lightColorScheme(
    primary = Green400,
    secondary = Green200,
    secondaryContainer = Green200,
    onPrimary = Black900,
    onSecondary = Black900,
    onSecondaryContainer = Green400,
    background = White100,
    onBackground = Black900,
    surface = White200,
    onSurface = Black900,
    surfaceVariant = Grey200,
    onSurfaceVariant = Black700,
    outline = Grey400,
    outlineVariant = Grey200,
    error = Red400,
    errorContainer = Red400,
    onErrorContainer = White100
)

private val LightBlueColorScheme = LightGreenColorScheme.copy(
    primary = Blue400,
    secondary = Blue200,
    secondaryContainer = Blue200,
    onSecondaryContainer = Blue400,
)

private val LightOrangeColorScheme = LightGreenColorScheme.copy(
    primary = Orange500,
    secondary = Orange200,
    secondaryContainer = Orange200,
    onSecondaryContainer = Orange500,
)

// --- Темные цветовые схемы ---

private val DarkGreenColorScheme = darkColorScheme(
    primary = DarkGreenPrimary,
    secondary = DarkGreenSurface,
    secondaryContainer = DarkGreenSurface,
    surface = DarkBackgroundBar,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSurface,
    onSecondaryContainer = DarkOnSurface,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkGreenSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = DarkError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnError
)

private val DarkBlueColorScheme = DarkGreenColorScheme.copy(
    primary = DarkBluePrimary,
    secondary = DarkBlueSurface,
    secondaryContainer = DarkBlueSurface,
    surfaceVariant = DarkBlueSurface,
)

private val DarkOrangeColorScheme = DarkGreenColorScheme.copy(
    primary = DarkOrangePrimary,
    secondary = DarkOrangeSurface,
    secondaryContainer = DarkOrangeSurface,
    surfaceVariant = DarkOrangeSurface,
)

/**
 * Фабричная функция, возвращающая нужную ColorScheme в зависимости от настроек.
 */
fun getBudgetlyApplicationScheme(themeColor: ThemeColor, darkTheme: Boolean): ColorScheme {
    return when (themeColor) {
        ThemeColor.GREEN -> if (darkTheme) DarkGreenColorScheme else LightGreenColorScheme
        ThemeColor.BLUE -> if (darkTheme) DarkBlueColorScheme else LightBlueColorScheme
        ThemeColor.ORANGE -> if (darkTheme) DarkOrangeColorScheme else LightOrangeColorScheme
    }
}


/**
 * Основная тема приложения Budgetly.
 * Применяет цветовые схемы, типографику, формы и настраивает системные бары.
 *
 * @param darkTheme Использовать ли темную тему. По умолчанию определяется системой.
 * @param dynamicColor Использовать ли динамические цвета (Material You). Доступно на Android 12+.
 * @param themeColor Выбранный пользователем основной цвет темы.
 * @param content Контент, к которому применяется тема.
 */
@Composable
fun BudgetlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themeColor: ThemeColor = ThemeColor.GREEN,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getBudgetlyApplicationScheme(themeColor, darkTheme)
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