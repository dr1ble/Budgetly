package shmr.budgetly.ui.screens.settings.pincode

import androidx.annotation.StringRes
import shmr.budgetly.R

/**
 * Состояние UI для экрана ввода/установки пин-кода.
 */
data class PinCodeUiState(
    val mode: PinCodeMode = PinCodeMode.LOADING,
    val enteredPin: String = "",
    val error: PinCodeError? = null,
    val isPinVerified: Boolean = false,
    val isPinCreated: Boolean = false,
    val isPinCleared: Boolean = false,
)

/**
 * Режимы работы экрана пин-кода.
 */
enum class PinCodeMode(@StringRes val titleRes: Int, val isCancellable: Boolean = false) {
    LOADING(R.string.pincode_check_title),
    CHECK_TO_UNLOCK(R.string.pincode_check_title),
    CHECK_TO_CHANGE(R.string.pincode_check_to_change_title, isCancellable = true),
    CHECK_TO_DELETE(R.string.pincode_delete_title, isCancellable = true),
    CREATE_STEP_1(R.string.pincode_create_title, isCancellable = true),
    CREATE_STEP_2(R.string.pincode_confirm_title, isCancellable = true)
}

/**
 * Ошибки, которые могут возникнуть на экране.
 */
enum class PinCodeError(@StringRes val messageRes: Int) {
    PIN_MISMATCH(R.string.pincode_error_mismatch),
    INCORRECT_PIN(R.string.pincode_error_incorrect)
}