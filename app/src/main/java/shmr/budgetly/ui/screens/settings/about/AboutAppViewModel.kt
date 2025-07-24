package shmr.budgetly.ui.screens.settings.about

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import shmr.budgetly.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AboutAppViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AboutAppUiState())
    val uiState = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    init {
        val version = BuildConfig.VERSION_NAME
        val updateTime = try {
            val timestamp = BuildConfig.BUILD_TIME.toLong()
            dateFormatter.format(Date(timestamp))
        } catch (e: NumberFormatException) {
            "N/A"
        }

        _uiState.value = AboutAppUiState(
            appVersion = version,
            lastUpdateTime = updateTime
        )
    }
}