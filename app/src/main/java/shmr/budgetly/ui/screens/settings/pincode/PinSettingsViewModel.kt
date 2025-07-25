package shmr.budgetly.ui.screens.settings.pincode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import shmr.budgetly.domain.events.AppEvent
import shmr.budgetly.domain.events.AppEventBus
import shmr.budgetly.domain.usecase.IsPinSetUseCase
import javax.inject.Inject

class PinSettingsViewModel @Inject constructor(
    private val isPinSetUseCase: IsPinSetUseCase,
    private val appEventBus: AppEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinSettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkPinStatus()

        viewModelScope.launch {
            appEventBus.events.collect { event ->
                if (event is AppEvent.PinStatusChanged) {
                    checkPinStatus()
                }
            }
        }
    }

    private fun checkPinStatus() {
        viewModelScope.launch {
            val isPinSet = isPinSetUseCase()
            _uiState.value = _uiState.value.copy(isPinSet = isPinSet)
        }
    }
}