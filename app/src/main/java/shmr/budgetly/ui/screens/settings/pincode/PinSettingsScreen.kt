package shmr.budgetly.ui.screens.settings.pincode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.navigation.PinScreenPurpose
import shmr.budgetly.ui.util.LocalTopAppBarSetter

@Composable
fun PinSettingsScreen(
    viewModel: PinSettingsViewModel,
    navController: NavController,
    onNavigateToPin: (purpose: PinScreenPurpose) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.setting_passcode),
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        val isPinSet = uiState.isPinSet
        if (isPinSet == null) {
            CircularProgressIndicator()
        } else {
            PinSettingsContent(
                isPinSet = isPinSet,
                onNavigateToPin = onNavigateToPin
            )
        }
    }
}

@Composable
private fun PinSettingsContent(
    isPinSet: Boolean,
    onNavigateToPin: (purpose: PinScreenPurpose) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (isPinSet) {
            item {
                BaseListItem(
                    title = stringResource(R.string.setting_passcode_change),
                    trail = {
                        Icon(
                            painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                            contentDescription = null
                        )
                    },
                    onClick = { onNavigateToPin(PinScreenPurpose.SETUP) }
                )
            }
            item {
                BaseListItem(
                    title = stringResource(R.string.setting_passcode_delete),
                    trail = {
                        Icon(
                            painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                            contentDescription = null
                        )
                    },
                    onClick = { onNavigateToPin(PinScreenPurpose.DELETE) }
                )
            }
        } else {
            item {
                BaseListItem(
                    title = stringResource(R.string.setting_passcode_set),
                    subtitle = stringResource(R.string.setting_passcode_subtitle_not_set),
                    trail = {
                        Icon(
                            painter = painterResource(R.drawable.ic_list_item_settings_arrow_right),
                            contentDescription = null
                        )
                    },
                    onClick = { onNavigateToPin(PinScreenPurpose.SETUP) }
                )
            }
        }
    }
}