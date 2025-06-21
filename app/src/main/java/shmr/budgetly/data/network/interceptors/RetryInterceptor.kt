package shmr.budgetly.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 2000L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null
        var tryCount = 0

        while (tryCount < MAX_RETRIES && (response == null || !response.isSuccessful && response.code >= 500)) {
            // Закрываем предыдущий неудачный ответ, если он есть, чтобы избежать утечки ресурсов
            response?.close()

            try {
                response = chain.proceed(request)
                // Если ответ успешный или ошибка не 5xx, выходим из цикла
                if (response.isSuccessful || response.code < 500) {
                    return response
                }
            } catch (e: IOException) {
                exception = e
                // Если произошла ошибка сети (например, таймаут), тоже пробуем снова
            }

            tryCount++

            if (tryCount < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw IOException(e)
                }
            }
        }

        // Если все попытки провалились, возвращаем последний неудачный ответ или пробрасываем исключение
        return response ?: throw exception ?: IOException("Unknown error")
    }
}