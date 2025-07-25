package shmr.budgetly.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.navigation.AboutApp
import shmr.budgetly.ui.navigation.ColorPicker
import shmr.budgetly.ui.navigation.Haptics
import shmr.budgetly.ui.navigation.Language
import shmr.budgetly.ui.navigation.PinSettings
import shmr.budgetly.ui.navigation.SyncSettings
import shmr.budgetly.ui.util.LocalTopAppBarSetter

@OptIn(ExperimentalMaterial3Api::class)
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
            .testTag("SettingsScreenContainer") // Добавляем testTag для поиска в тесте
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
                                modifier = Modifier.testTag("DarkThemeSwitch"),
                                onCheckedChange = viewModel::onThemeChanged
                            )
                        }
                    )
                }

                SettingType.PRIMARY_COLOR -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        trail = {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = null
                            )
                        },
                        onClick = { navController.navigate(ColorPicker) }
                    )
                }

                SettingType.NAVIGATION_HAPTICS -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        trail = {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = null
                            )
                        },
                        onClick = { navController.navigate(Haptics) }
                    )
                }

                SettingType.NAVIGATION_PINCODE -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        trail = {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = null
                            )
                        },
                        onClick = { navController.navigate(PinSettings) }
                    )
                }

                SettingType.NAVIGATION -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        trail = {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = null
                            )
                        },
                        onClick = { }
                    )
                }

                SettingType.SYNC_INFO -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        subtitle = uiState.lastSyncTime,
                        trail = {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = null
                            )
                        },
                        onClick = { navController.navigate(SyncSettings) }
                    )
                }

                SettingType.NAVIGATION_LANGUAGE -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        trail = {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = null
                            )
                        },
                        onClick = { navController.navigate(Language) }
                    )
                }

                SettingType.NAVIGATION_ABOUT -> {
                    BaseListItem(
                        title = stringResource(id = item.titleRes),
                        trail = {
                            Icon(
                                painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                                contentDescription = null
                            )
                        },
                        onClick = { navController.navigate(AboutApp) }
                    )
                }
            }
        }
    }
}