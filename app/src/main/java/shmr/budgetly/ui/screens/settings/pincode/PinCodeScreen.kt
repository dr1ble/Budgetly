package shmr.budgetly.ui.screens.settings.pincode

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shmr.budgetly.R
import kotlin.math.roundToInt

private const val PIN_LENGTH = 4

@Composable
fun PinCodeScreen(
    viewModel: PinCodeViewModel,
    onPinVerified: () -> Unit,
    onPinCreated: () -> Unit,
    onCancel: () -> Unit,
    onPinCleared: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isPinVerified) {
        LaunchedEffect(Unit) { onPinVerified() }
    }
    if (uiState.isPinCreated) {
        LaunchedEffect(Unit) { onPinCreated() }
    }

    if (uiState.isPinCleared) {
        LaunchedEffect(Unit) { onPinCleared() }
    }

    val shake = remember { Animatable(0f) }
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            shake.animateTo(targetValue = 15f, animationSpec = tween(durationMillis = 50))
            shake.animateTo(targetValue = -15f, animationSpec = tween(durationMillis = 50))
            shake.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 500))
        }
    }

    if (uiState.mode == PinCodeMode.LOADING) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            if (uiState.mode.isCancellable) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 32.dp)
                        .clickable { onCancel() }
                )
            }
        }

        Text(
            text = stringResource(id = uiState.mode.titleRes),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        PinIndicators(
            count = uiState.enteredPin.length,
            isError = uiState.error != null,
            modifier = Modifier.offset { IntOffset(shake.value.roundToInt(), 0) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = uiState.error?.let { stringResource(id = it.messageRes) } ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(40.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        PinKeyboard(
            onNumberClick = viewModel::onNumberClick,
            onDeleteClick = viewModel::onDeleteClick
        )
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun PinIndicators(count: Int, isError: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        repeat(PIN_LENGTH) { index ->
            PinIndicator(
                isFilled = index < count,
                isError = isError
            )
        }
    }
}

@Composable
private fun PinIndicator(isFilled: Boolean, isError: Boolean) {
    val color = when {
        isError -> MaterialTheme.colorScheme.error
        isFilled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun PinKeyboard(
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PinKey(number = 1, onClick = onNumberClick)
            PinKey(number = 2, onClick = onNumberClick)
            PinKey(number = 3, onClick = onNumberClick)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PinKey(number = 4, onClick = onNumberClick)
            PinKey(number = 5, onClick = onNumberClick)
            PinKey(number = 6, onClick = onNumberClick)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PinKey(number = 7, onClick = onNumberClick)
            PinKey(number = 8, onClick = onNumberClick)
            PinKey(number = 9, onClick = onNumberClick)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Spacer(modifier = Modifier.size(72.dp))
            PinKey(number = 0, onClick = onNumberClick)
            PinKey(isDelete = true, onClick = { onDeleteClick() })
        }
    }
}

@Composable
private fun PinKey(
    number: Int = 0,
    isDelete: Boolean = false,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable { onClick(number) },
        contentAlignment = Alignment.Center
    ) {
        if (isDelete) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.pincode_delete_button_description),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(28.dp)
            )
        } else {
            Text(
                text = number.toString(),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}