package shmr.budgetly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shmr.budgetly.R
import shmr.budgetly.data.MockData
import shmr.budgetly.ui.components.BaseListItem

@Preview
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var isDarkThemeEnabled by remember { mutableStateOf(false) }
    val settings = MockData.settingsItems

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items(
            items = settings,
            key = { it }
        ) { settingTitle ->
            BaseListItem(
                defaultHeight = 56.dp,
                title = settingTitle,
                titleTextStyle = MaterialTheme.typography.bodyLarge,
                showDivider = true,
                onClick = {
                    when {
                        settingTitle != "Тёмная тема" -> {
                        }
                    }
                },
                trail = {
                    when (settingTitle) {
                        "Тёмная тема" -> {
                            Switch(
                                checked = isDarkThemeEnabled,
                                onCheckedChange = { isDarkThemeEnabled = it }
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