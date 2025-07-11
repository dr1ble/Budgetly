package shmr.budgetly.ui.screens.account.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.util.LocalTopAppBarSetter
import shmr.budgetly.ui.util.formatCurrencySymbol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    viewModel: EditAccountViewModel,
    onSaveSuccess: () -> Unit,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    LaunchedEffect(uiState) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(R.string.edit_account_top_bar_title),
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() },
                actions = {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = viewModel::saveAccount,
                            enabled = uiState.isSaveEnabled
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_top_bar_confirm),
                                contentDescription = stringResource(R.string.save_action_description)
                            )
                        }
                    }
                }
            )
        }
    }

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            onSaveSuccess()
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (uiState.isBottomSheetVisible) {
        CurrencyBottomSheet(
            onDismiss = viewModel::onCurrencyPickerDismiss,
            onCurrencySelected = viewModel::onCurrencyChange,
            sheetState = sheetState
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isInitialLoading -> CircularProgressIndicator()
            uiState.error != null -> ErrorState(
                message = stringResource(R.string.error_unknown),
                onRetry = viewModel::loadInitialData
            )
            uiState.name != null && uiState.balance != null && uiState.currency != null -> {
                EditAccountContent(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun EditAccountContent(
    uiState: EditAccountUiState,
    viewModel: EditAccountViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            NameItem(
                name = uiState.name!!,
                onNameChange = viewModel::onNameChange
            )
        }
        item {
            BalanceItem(
                balance = uiState.balance!!,
                onBalanceChange = viewModel::onBalanceChange
            )
        }
        item {
            CurrencyItem(
                currency = uiState.currency!!,
                onClick = viewModel::onCurrencyPickerShow
            )
        }
    }
}

@Composable
private fun NameItem(name: String, onNameChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EmojiIcon(emoji = "💰", size = 24.dp)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.edit_account_name_label),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        BasicTextField(
            value = name,
            onValueChange = onNameChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f)
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun BalanceItem(balance: String, onBalanceChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.edit_account_balance_label),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        BasicTextField(
            value = balance,
            onValueChange = onBalanceChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f)
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun CurrencyItem(currency: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.edit_account_currency_label),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatCurrencySymbol(currency),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_list_item_trail_arrow),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyBottomSheet(
    onDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    val currencies = mapOf(
        "RUB" to "Российский рубль",
        "USD" to "Американский доллар",
        "EUR" to "Евро"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn {
            items(currencies.entries.toList()) { (code, name) ->
                BaseListItem(
                    lead = {
                        Text(
                            text = formatCurrencySymbol(code),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.width(24.dp),
                            textAlign = TextAlign.Center
                        )
                    },
                    title = name,
                    trail = null,
                    showDivider = true,
                    onClick = {
                        onCurrencySelected(code)
                        onDismiss()
                    }
                )
            }
            item {
                BaseListItem(
                    lead = {
                        Icon(
                            painter = painterResource(R.drawable.ic_bottom_sheet_cancel),
                            contentDescription = stringResource(R.string.edit_account_currency_change_cancel),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    },
                    title = stringResource(R.string.dialog_cancel),
                    titleTextStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    showDivider = false,
                    onClick = onDismiss
                )
            }
        }
    }
}