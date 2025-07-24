package shmr.budgetly.ui.screens.settings.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.util.LocalTopAppBarSetter

@Composable
fun SyncSettingsScreen(
    viewModel: SyncSettingsViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.setting_sync_frequency),
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = uiState.selectedInterval.labelRes),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Slider(
            value = uiState.sliderPosition,
            onValueChange = viewModel::onSliderPositionChange,
            onValueChangeFinished = viewModel::onSliderChangeFinished,
            steps = uiState.availableIntervals.size - 2, // кол-во шагов = кол-во опций - 2
            valueRange = 0f..(uiState.availableIntervals.size - 1).toFloat()
        )
    }
}