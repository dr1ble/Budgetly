package shmr.budgetly.ui.screens.pincode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.di.viewmodel.AssistedSavedStateViewModelFactory
import shmr.budgetly.domain.usecase.CheckPinUseCase
import shmr.budgetly.domain.usecase.ClearPinUseCase
import shmr.budgetly.domain.usecase.IsPinSetUseCase
import shmr.budgetly.domain.usecase.SavePinUseCase
import shmr.budgetly.ui.navigation.Pin
import shmr.budgetly.ui.navigation.PinScreenPurpose

class PinCodeViewModel @AssistedInject constructor(
    private val isPinSetUseCase: IsPinSetUseCase,
    private val savePinUseCase: SavePinUseCase,
    private val checkPinUseCase: CheckPinUseCase,
    private val clearPinUseCase: ClearPinUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinCodeUiState())
    val uiState = _uiState.asStateFlow()

    private var tempPin: String? = null

    init {
        determineInitialMode()
    }

    private fun determineInitialMode() {
        viewModelScope.launch {
            val navArgs: Pin = savedStateHandle.toRoute()
            val isPinSet = isPinSetUseCase()

            val initialMode = when (navArgs.purpose) {
                PinScreenPurpose.UNLOCK -> PinCodeMode.CHECK_TO_UNLOCK
                PinScreenPurpose.SETUP -> if (isPinSet) PinCodeMode.CHECK_TO_CHANGE else PinCodeMode.CREATE_STEP_1
                PinScreenPurpose.DELETE -> PinCodeMode.CHECK_TO_DELETE
            }
            _uiState.update { it.copy(mode = initialMode) }
        }
    }

    fun onNumberClick(number: Int) {
        if (_uiState.value.enteredPin.length >= PIN_LENGTH) return
        _uiState.update { it.copy(enteredPin = it.enteredPin + number, error = null) }

        if (_uiState.value.enteredPin.length == PIN_LENGTH) {
            processPinInput()
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(enteredPin = it.enteredPin.dropLast(1), error = null) }
    }

    private fun processPinInput() {
        val currentPin = _uiState.value.enteredPin
        viewModelScope.launch {
            when (_uiState.value.mode) {
                PinCodeMode.CREATE_STEP_1 -> {
                    tempPin = currentPin
                    _uiState.update { it.copy(mode = PinCodeMode.CREATE_STEP_2, enteredPin = "") }
                }

                PinCodeMode.CREATE_STEP_2 -> {
                    if (currentPin == tempPin) {
                        savePinUseCase(currentPin)
                        _uiState.update { it.copy(isPinCreated = true) }
                    } else {
                        tempPin = null
                        _uiState.update {
                            it.copy(
                                mode = PinCodeMode.CREATE_STEP_1,
                                enteredPin = "",
                                error = PinCodeError.PIN_MISMATCH
                            )
                        }
                    }
                }

                PinCodeMode.CHECK_TO_UNLOCK -> {
                    val isCorrect = checkPinUseCase(currentPin)
                    if (isCorrect) {
                        _uiState.update { it.copy(isPinVerified = true) }
                    } else {
                        _uiState.update {
                            it.copy(
                                enteredPin = "",
                                error = PinCodeError.INCORRECT_PIN
                            )
                        }
                    }
                }

                PinCodeMode.CHECK_TO_CHANGE -> {
                    val isCorrect = checkPinUseCase(currentPin)
                    if (isCorrect) {
                        _uiState.update {
                            it.copy(
                                mode = PinCodeMode.CREATE_STEP_1,
                                enteredPin = ""
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                enteredPin = "",
                                error = PinCodeError.INCORRECT_PIN
                            )
                        }
                    }
                }

                PinCodeMode.CHECK_TO_DELETE -> {
                    val isCorrect = checkPinUseCase(currentPin)
                    if (isCorrect) {
                        clearPinUseCase()
                        _uiState.update { it.copy(isPinCleared = true) }
                    } else {
                        _uiState.update {
                            it.copy(
                                enteredPin = "",
                                error = PinCodeError.INCORRECT_PIN
                            )
                        }
                    }
                }

                PinCodeMode.LOADING -> {}
            }
        }
    }

    private companion object {
        const val PIN_LENGTH = 4
    }

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<PinCodeViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): PinCodeViewModel
    }
}