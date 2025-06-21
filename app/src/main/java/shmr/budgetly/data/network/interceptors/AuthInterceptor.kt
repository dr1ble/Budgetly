package shmr.budgetly.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import shmr.budgetly.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An OkHttp Interceptor that adds the Authorization header to requests.
 */
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    private var authToken: String? = BuildConfig.API_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = authToken?.let { token ->
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } ?: originalRequest

        return chain.proceed(newRequest)
    }
}