package shmr.budgetly.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Перехватчик OkHttp, который автоматически повторяет неудавшиеся запросы.
 * Повторные попытки выполняются при возникновении [IOException] (ошибки сети)
 * или при получении ответа с кодом 5xx (ошибки сервера).
 */
@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {

    private companion object {
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 2000L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var lastException: IOException? = null
        var tryCount = 0

        while (tryCount < MAX_RETRIES && !isSuccessful(response)) {
            // Закрываем предыдущий неудачный ответ, чтобы избежать утечки ресурсов
            response?.close()
            try {
                response = chain.proceed(
                    request.newBuilder().build()
                ) // Используем newBuilder, чтобы запрос был свежим
                if (isSuccessful(response)) return response
            } catch (e: IOException) {
                lastException = e
            }

            tryCount++
            if (tryCount < MAX_RETRIES) {
                sleep()
            }
        }

        // Если все попытки провалились, возвращаем последний неудачный ответ
        // или пробрасываем последнее пойманное исключение.
        return response ?: throw lastException
            ?: IOException("Unknown error after $MAX_RETRIES retries")
    }

    /**
     * Проверяет, является ли ответ успешным (2xx) или ошибкой, не требующей повтора (не 5xx).
     */
    private fun isSuccessful(response: Response?): Boolean {
        return response != null && (response.isSuccessful || response.code < 500)
    }

    /**
     * Выполняет задержку перед следующей попыткой.
     */
    private fun sleep() {
        try {
            Thread.sleep(RETRY_DELAY_MS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Retry interrupted", e)
        }
    }
}