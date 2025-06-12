package shmr.budgetly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

val MaterialTheme.dimens: Dimens
    @Composable get() = LocalDimens.current
