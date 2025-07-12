package shmr.budgetly.data.network.interceptors

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import okhttp3.Interceptor
import okhttp3.Response
import shmr.budgetly.data.network.exception.NoConnectivityException
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

/**
 * Перехватчик OkHttp, который проверяет наличие активного и валидного интернет-соединения
 * перед выполнением сетевого запроса. Если соединение отсутствует или не предоставляет
 * доступ в интернет, выбрасывает [NoConnectivityException].
 */
@AppScope
class ConnectivityInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }

    /**
     * Проверяет, доступно ли валидное сетевое подключение.
     * Простого наличия Wi-Fi или мобильной сети недостаточно; система должна
     * подтвердить, что через это соединение есть реальный доступ в интернет.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}