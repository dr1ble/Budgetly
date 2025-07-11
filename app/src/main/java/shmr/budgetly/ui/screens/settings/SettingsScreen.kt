package shmr.budgetly.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.util.LocalTopAppBarSetter

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    navController: NavController
) {
    val topAppBarSetter = LocalTopAppBarSetter.current
    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(title = stringResource(id = R.string.settings_top_bar_title))
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(
            items = uiState.settingsItems,
            key = { it.id }
        ) { item ->
            when (item.type) {
                SettingType.THEME_SWITCH -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        trail = {
                            Switch(
                                checked = uiState.isDarkThemeEnabled,
                                onCheckedChange = viewModel::onThemeChanged
                            )
                        }
                    )
                }
                SettingType.NAVIGATION -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        onClick = { /* TODO: Implement navigation to sub-settings */ }
                    )
                }
            }
        }
    }
}