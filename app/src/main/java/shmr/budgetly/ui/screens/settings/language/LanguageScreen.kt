package shmr.budgetly.ui.screens.settings.language

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.domain.model.Language
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.util.LocalTopAppBarSetter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(Unit) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.setting_language),
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
        items(
            items = uiState.availableLanguages,
            key = { it.name }
        ) { language ->
            LanguageItem(
                language = language,
                isSelected = language == uiState.selectedLanguage,
                onClick = { viewModel.onLanguageSelected(language) }
            )
        }
    }
}

@Composable
private fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    BaseListItem(
        title = language.nativeName,
        onClick = onClick,
        trail = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.language_is_selected_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}