package shmr.budgetly.ui.screens.settings.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.domain.model.ThemeColor
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.theme.defaultDimens
import shmr.budgetly.ui.theme.getBudgetlyApplicationScheme
import shmr.budgetly.ui.util.LocalTopAppBarSetter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerScreen(
    viewModel: ColorPickerViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.setting_primary_color),
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 8.dp)
    ) {
        items(
            items = uiState.availableColors,
            key = { it.name }
        ) { color ->
            ColorItem(
                themeColor = color,
                isSelected = color == uiState.selectedColor,
                onClick = { viewModel.onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun ColorItem(
    themeColor: ThemeColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = getBudgetlyApplicationScheme(themeColor = themeColor, darkTheme = false)
    val colorName = when (themeColor) {
        ThemeColor.GREEN -> stringResource(R.string.color_name_green)
        ThemeColor.BLUE -> stringResource(R.string.color_name_blue)
        ThemeColor.ORANGE -> stringResource(R.string.color_name_orange)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(defaultDimens.heights.normal)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ColorCircle(color = colorScheme.primary)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = colorName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.color_is_selected_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
@Composable
private fun ColorCircle(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
    )
}