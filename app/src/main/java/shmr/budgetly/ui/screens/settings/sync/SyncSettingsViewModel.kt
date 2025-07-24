package shmr.budgetly.ui.screens.settings.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shmr.budgetly.domain.model.SyncInterval
import shmr.budgetly.domain.usecase.GetSyncIntervalUseCase
import shmr.budgetly.domain.usecase.SetSyncIntervalUseCase
import javax.inject.Inject

class SyncSettingsViewModel @Inject constructor(
    private val getSyncIntervalUseCase: GetSyncIntervalUseCase,
    private val setSyncIntervalUseCase: SetSyncIntervalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncSettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getSyncIntervalUseCase()
            .onEach { interval ->
                val position = SyncInterval.entries.indexOf(interval).toFloat()
                _uiState.update {
                    it.copy(
                        selectedInterval = interval,
                        sliderPosition = position
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Вызывается при изменении положения слайдера.
     */
    fun onSliderPositionChange(newPosition: Float) {
        val index = newPosition.toInt()
        val interval = SyncInterval.entries[index]
        _uiState.update {
            it.copy(
                sliderPosition = newPosition,
                selectedInterval = interval
            )
        }
    }

    /**
     * Вызывается, когда пользователь закончил взаимодействие со слайдером.
     * Сохраняет выбранное значение.
     */
    fun onSliderChangeFinished() {
        viewModelScope.launch {
            setSyncIntervalUseCase(_uiState.value.selectedInterval)
        }
    }
}