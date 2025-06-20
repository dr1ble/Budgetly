package shmr.budgetly.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val spacing: Spacing = Spacing(),
    val sizes: Sizes = Sizes(),
    val heights: Heights = Heights()
)

@Immutable
data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
)

@Immutable
data class Heights(
    val small: Dp = 56.dp,
    val normal: Dp = 70.dp
)

@Immutable
data class Sizes(
    val icon: Dp = 24.dp
)

val defaultDimens = Dimens()

val LocalDimens = staticCompositionLocalOf { defaultDimens }