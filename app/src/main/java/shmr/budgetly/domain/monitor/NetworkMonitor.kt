package shmr.budgetly.domain.monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.events.AppEvent
import shmr.budgetly.domain.events.AppEventBus
import javax.inject.Inject

/**
 * Отслеживает состояние сети в приложении.
 * При восстановлении интернет-соединения после его отсутствия
 * отправляет событие [AppEvent.NetworkAvailable] через [AppEventBus].
 */
@AppScope
class NetworkMonitor @Inject constructor(
    private val context: Context,
    private val appEventBus: AppEventBus
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var isNetworkAvailable = false
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            checkNetworkState()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            checkNetworkState()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            checkNetworkState()
        }
    }

    private fun checkNetworkState() {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val hasInternet =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

        // Отправляем событие, только если интернет ПОЯВИЛСЯ (предыдущее состояние было "нет интернета")
        if (hasInternet && !isNetworkAvailable) {
            scope.launch {
                appEventBus.postEvent(AppEvent.NetworkAvailable)
            }
        }
        isNetworkAvailable = hasInternet
    }

    /**
     * Запускает мониторинг сети. Должен вызываться при старте приложения.
     */
    fun start() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Проверяем начальное состояние
        checkNetworkState()
    }

    /**
     * Останавливает мониторинг сети.
     */
    @Suppress("unused")
    fun stop() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}