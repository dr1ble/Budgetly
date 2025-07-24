package shmr.budgetly.ui.screens.settings.haptics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.domain.model.HapticEffect
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.util.LocalTopAppBarSetter

@Composable
fun HapticsScreen(
    viewModel: HapticsViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.setting_haptics),
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            BaseListItem(
                title = stringResource(id = R.string.haptics_enable_toggle),
                trail = {
                    Switch(
                        checked = uiState.isEnabled,
                        onCheckedChange = viewModel::onHapticsEnabledChanged
                    )
                }
            )
        }
        item {
            Spacer(modifier = Modifier.padding(top = 16.dp))
        }

        // Список эффектов
        items(
            items = uiState.availableEffects,
            key = { it.name }
        ) { effect ->
            EffectItem(
                effect = effect,
                isSelected = effect == uiState.selectedEffect,
                isEnabled = uiState.isEnabled,
                onClick = { viewModel.onEffectSelected(effect) }
            )
        }
    }
}

@Composable
private fun EffectItem(
    effect: HapticEffect,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val effectName = when (effect) {
        HapticEffect.CLICK -> stringResource(R.string.haptic_effect_click)
        HapticEffect.TICK -> stringResource(R.string.haptic_effect_tick)
        HapticEffect.DOUBLE_CLICK -> stringResource(R.string.haptic_effect_double_click)
    }

    BaseListItem(
        title = effectName,
        modifier = Modifier
            .alpha(if (isEnabled) 1f else 0.5f) // Делаем элемент полупрозрачным, если вибрация выключена
            .clickable(enabled = isEnabled, onClick = onClick),
        trail = {
            if (isSelected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.haptic_effect_is_selected_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp)) // Добавляем отступ для выравнивания с Switch
                }
            }
        },
        showDivider = true
    )
}