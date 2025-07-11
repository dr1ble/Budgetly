package shmr.budgetly.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject
import javax.inject.Named

/**
 * Перехватчик OkHttp, который добавляет заголовок "Authorization" с Bearer-токеном
 * ко всем исходящим сетевым запросам.
 */
@AppScope
class AuthInterceptor @Inject constructor(
    @Named("apiToken") private val authToken: String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Добавляем заголовок, только если токен существует и не пуст
        val newRequest = authToken?.takeIf { it.isNotEmpty() }?.let { token ->
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } ?: originalRequest

        return chain.proceed(newRequest)
    }
}