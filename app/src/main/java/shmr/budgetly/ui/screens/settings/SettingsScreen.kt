package shmr.budgetly.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shmr.budgetly.R
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.theme.dimens

/**
 * Экран настроек приложения.
 * Отображает список доступных настроек, таких как переключение темы,
 * и позволяет пользователю взаимодействовать с ними.
 *
 * @param modifier Модификатор для настройки внешнего вида и поведения.
 * @param viewModel ViewModel для управления состоянием экрана.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(
            items = uiState.settingsItems,
            key = { it.id }
        ) { settingItem ->
            SettingsItem(
                item = settingItem,
                isDarkThemeEnabled = uiState.isDarkThemeEnabled,
                onThemeChanged = viewModel::onThemeChanged
            )
        }
    }
}

/**
 * Отображает один элемент списка настроек.
 */
@Composable
private fun SettingsItem(
    item: SettingItem,
    isDarkThemeEnabled: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    BaseListItem(
        defaultHeight = MaterialTheme.dimens.heights.small,
        title = stringResource(id = item.titleRes),
        titleTextStyle = MaterialTheme.typography.bodyLarge,
        showDivider = true,
        onClick = {}, // TODO
        trail = {
            when (item.type) {
                SettingType.THEME_SWITCH -> {
                    Switch(
                        checked = isDarkThemeEnabled,
                        onCheckedChange = onThemeChanged
                    )
                }

                SettingType.NAVIGATION -> {
                    Icon(
                        painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                        contentDescription = stringResource(R.string.navigate_action_description)
                    )
                }
            }
        }
    )
}