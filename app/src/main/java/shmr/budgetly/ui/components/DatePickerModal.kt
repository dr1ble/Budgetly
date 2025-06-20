package shmr.budgetly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import shmr.budgetly.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate: Long,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    DatePickerDialog(
        shape = RoundedCornerShape(16.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            DatePickerButtons(
                onDateSelected = onDateSelected,
                onDismiss = onDismiss,
                datePickerState = datePickerState
            )
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondary,
        )
    ) {
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondary,
                // Цвет для выбранного дня
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                // Цвет для выбранного года
                selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                // Цвет для сегодняшней даты (если она не выбрана)
                todayContentColor = MaterialTheme.colorScheme.primary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButtons(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: DatePickerState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        PickerButton(
            buttonType = ButtonType.CLEAR,
            onDismiss = onDismiss,
            onDateSelected = onDateSelected
        )

        Row {

            PickerButton(buttonType = ButtonType.CANCEL, onDismiss = onDismiss)


            PickerButton(
                buttonType = ButtonType.OK,
                onDismiss = onDismiss,
                onDateSelected = onDateSelected,
                datePickerState = datePickerState
            )
        }
    }
}

enum class ButtonType {
    OK,
    CANCEL,
    CLEAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerButton(
    buttonType: ButtonType,
    onDismiss: () -> Unit,
    onDateSelected: ((Long?) -> Unit)? = null,
    datePickerState: DatePickerState? = null
) {

    val text = when (buttonType) {
        ButtonType.OK -> stringResource(R.string.dialog_ok)
        ButtonType.CANCEL -> stringResource(R.string.dialog_cancel)
        ButtonType.CLEAR -> stringResource(R.string.dialog_clear)
    }

    val onClickAction: () -> Unit = when (buttonType) {
        ButtonType.OK -> {
            {
                onDateSelected?.invoke(datePickerState?.selectedDateMillis)
                onDismiss()
            }
        }

        ButtonType.CANCEL -> {
            {
                onDismiss()
            }
        }

        ButtonType.CLEAR -> {
            {
                onDateSelected?.invoke(null)
                onDismiss()
            }
        }
    }

    TextButton(
        onClick = onClickAction,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text)
    }
}