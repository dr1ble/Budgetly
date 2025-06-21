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
            key = { it }
        ) { settingTitle ->
            BaseListItem(
                defaultHeight = MaterialTheme.dimens.heights.small,
                title = settingTitle,
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                showDivider = true,
                onClick = {},
                trail = {
                    when (settingTitle) {
                        stringResource(R.string.screen_settings_dark_theme_label) -> {
                            Switch(
                                checked = uiState.isDarkThemeEnabled,
                                onCheckedChange = viewModel::onThemeChanged
                            )
                        }

                        else -> {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = stringResource(R.string.account_ic_arrow_description)
                            )
                        }
                    }
                }
            )
        }
    }
}