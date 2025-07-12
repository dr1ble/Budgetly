package shmr.budgetly.ui.screens.transactiondetails

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import shmr.budgetly.R
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseListItem
import shmr.budgetly.ui.components.DatePickerModal
import shmr.budgetly.ui.components.EmojiIcon
import shmr.budgetly.ui.components.ErrorState
import shmr.budgetly.ui.components.TimePickerModal
import shmr.budgetly.ui.navigation.TransactionDetails
import shmr.budgetly.ui.theme.dimens
import shmr.budgetly.ui.util.LocalTopAppBarSetter
import shmr.budgetly.ui.util.formatCurrencySymbol
import shmr.budgetly.ui.util.getErrorMessage
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun TransactionDetailsScreen(
    viewModel: TransactionDetailsViewModel,
    navController: NavController,
    onSaveSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topAppBarSetter = LocalTopAppBarSetter.current

    val title = if (uiState.isEditMode) {
        if (uiState.isIncome) R.string.transaction_details_edit_income else R.string.transaction_details_edit_expense
    } else {
        if (uiState.isIncome) R.string.transaction_details_create_income else R.string.transaction_details_create_expense
    }

    LaunchedEffect(uiState.isSaving, uiState.isSaveEnabled) {
        topAppBarSetter {
            AppTopBar(
                title = stringResource(id = title),
                navigationIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) },
                onNavigationClick = { navController.popBackStack() },
                actions = {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        IconButton(
                            onClick = viewModel::onSaveClick,
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

    HandleDialogs(uiState, viewModel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> ErrorState(
                message = getErrorMessage(uiState.error!!),
                onRetry = {
                    if (uiState.isEditMode) {
                        viewModel.init(
                            TransactionDetails(
                                transactionId = viewModel.uiState.value.let { if (it.isEditMode) viewModel.transactionId else null },
                                isIncome = uiState.isIncome
                            )
                        )
                    } else {
                        viewModel.init(TransactionDetails(isIncome = uiState.isIncome))
                    }
                }
            )

            else -> TransactionDetailsContent(uiState, viewModel)
        }
    }
}

@Composable
private fun TransactionDetailsContent(
    uiState: TransactionDetailsUiState,
    viewModel: TransactionDetailsViewModel
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {

            BaseListItem(
                defaultHeight = MaterialTheme.dimens.heights.normal,
                title = stringResource(R.string.transaction_details_account),
                onClick = null,
                trail = {
                    Text(
                        text = uiState.selectedAccount?.name ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
        item {
            BaseListItem(
                defaultHeight = MaterialTheme.dimens.heights.normal,
                title = stringResource(R.string.transaction_details_category),
                onClick = viewModel::showCategoryPicker,
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.selectedCategory?.name ?: "",
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
            )
        }
        item {
            BaseListItem(
                defaultHeight = MaterialTheme.dimens.heights.normal,
                title = stringResource(R.string.transaction_details_amount),
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = uiState.amount,
                            onValueChange = viewModel::onAmountChange,
                            modifier = Modifier.width(100.dp), // Aдаптивная ширина для суммы
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.End
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = formatCurrencySymbol(uiState.selectedAccount?.currency ?: "₽"),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
        item {
            BaseListItem(
                defaultHeight = MaterialTheme.dimens.heights.normal,
                title = stringResource(R.string.transaction_details_date),
                onClick = viewModel::showDatePicker,
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
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
            )
        }
        item {
            BaseListItem(
                defaultHeight = MaterialTheme.dimens.heights.normal,
                title = stringResource(R.string.transaction_details_time),
                onClick = viewModel::showTimePicker,
                trail = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.time.format(DateTimeFormatter.ofPattern("HH:mm")),
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
            )
        }
        item {
            CommentItem(
                comment = uiState.comment,
                onCommentChange = viewModel::onCommentChange
            )
        }


        if (uiState.isEditMode) {
            item {
                Spacer(modifier = Modifier.padding(top = 16.dp))
                DeleteButton(
                    isIncome = uiState.isIncome,
                    onClick = viewModel::showDeleteConfirmDialog
                )
            }
        }
    }
}

@Composable
private fun CommentItem(comment: String, onCommentChange: (String) -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.heights.normal)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (comment.isEmpty()) {
                        Text(
                            text = stringResource(R.string.transaction_details_comment_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun DeleteButton(isIncome: Boolean, onClick: () -> Unit) {
    val textRes = if (isIncome) {
        R.string.transaction_details_delete_income_button
    } else {
        R.string.transaction_details_delete_expense_button
    }
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(
            text = stringResource(textRes),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HandleDialogs(
    uiState: TransactionDetailsUiState,
    viewModel: TransactionDetailsViewModel
) {
    if (uiState.isDatePickerVisible) {
        DatePickerModal(
            selectedDate = uiState.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
            onDateSelected = { millis ->
                millis?.let {
                    viewModel.onDateSelected(
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    )
                } ?: viewModel.dismissDatePicker()
            },
            onDismiss = viewModel::dismissDatePicker
        )
    }

    if (uiState.isTimePickerVisible) {
        TimePickerModal(
            time = uiState.time,
            onTimeSelected = viewModel::onTimeSelected,
            onDismiss = viewModel::dismissTimePicker
        )
    }

    if (uiState.isCategoryPickerVisible) {
        CategoryPickerBottomSheet(
            categories = uiState.availableCategories,
            onCategorySelected = viewModel::onCategorySelected,
            onDismiss = viewModel::dismissCategoryPicker,
        )
    }

    if (uiState.saveError != null) {
        AlertDialog(
            shape = MaterialTheme.shapes.small,
            onDismissRequest = viewModel::dismissSaveErrorDialog,
            title = { Text(text = stringResource(R.string.dialog_error_title)) },
            text = { Text(getErrorMessage(error = uiState.saveError)) },
            confirmButton = {
                TextButton(onClick = viewModel::onSaveClick) {
                    Text(stringResource(id = R.string.retry_button_text))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissSaveErrorDialog) {
                    Text(stringResource(id = R.string.dialog_cancel))
                }
            }
        )
    }

    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            shape = MaterialTheme.shapes.small,
            onDismissRequest = viewModel::dismissDeleteConfirmDialog,
            title = { Text(text = stringResource(R.string.transaction_details_delete_confirm_title)) },
            text = { Text(text = stringResource(R.string.transaction_details_delete_confirm_text)) },
            confirmButton = {
                TextButton(
                    onClick = viewModel::deleteTransaction,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.dialog_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteConfirmDialog) {
                    Text(stringResource(id = R.string.dialog_cancel))
                }
            }
        )
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPickerBottomSheet(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(modifier = Modifier.heightIn(max = screenHeight * 0.8f)) {
            items(categories, key = { it.id }) { category ->
                BaseListItem(
                    title = category.name,
                    lead = { EmojiIcon(emoji = category.emoji) },
                    onClick = {
                        onCategorySelected(category)
                        onDismiss()
                    }
                )
            }
        }
    }
}