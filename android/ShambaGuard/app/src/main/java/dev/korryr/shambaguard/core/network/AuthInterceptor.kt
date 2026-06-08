package dev.korryr.shambaguard.core.network

import dev.korryr.shambaguard.core.datastore.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Endpoints that don't need auth token
        if (originalRequest.url.encodedPath.contains("/api/farmers/login") ||
            originalRequest.url.encodedPath.contains("/api/farmers/register") ||
            originalRequest.url.encodedPath.contains("/api/farms/register")
        ) {
            return chain.proceed(originalRequest)
        }

        // Fetch JWT token synchronously
        val token = runBlocking {
            sessionManager.jwtTokenFlow.firstOrNull()
        }

        return if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
