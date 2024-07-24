package ru.porcupine.chattask.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.porcupine.chattask.util.TokenManager
import ru.porcupine.chattask.util.TokenRefreshManager

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val tokenRefreshManager: TokenRefreshManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()
        val requestWithToken = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(requestWithToken)

        if (response.code == 401) {
            synchronized(this) {
                response.close()
                val newAccessToken = runBlocking {
                    tokenRefreshManager.refreshToken()
                }
                tokenManager.saveAccessToken(newAccessToken)
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                response = chain.proceed(newRequest)
            }
        }
        return response
    }
}
