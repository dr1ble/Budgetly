package shmr.budgetly.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * An OkHttp Interceptor that adds the Authorization header to requests.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    @Named("apiToken") private val authToken: String?
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = authToken?.takeIf { it.isNotEmpty() }?.let { token ->
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } ?: originalRequest

        return chain.proceed(newRequest)
    }
}