package ru.porcupine.chattask.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class TokenRefreshManager(private val tokenManager: TokenManager) {

    private val client = OkHttpClient()

    suspend fun refreshToken(): String = withContext(Dispatchers.IO) {
        try {
            val requestBody = createRequestBody()
            val request = Request.Builder()
                .url("https://plannerok.ru/api/v1/users/refresh-token/")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                throw Exception("Failed to refresh token: $errorBody")
            }

            val responseBody = response.body?.string()
            val json = JSONObject(responseBody ?: "")

            if (!json.has("access_token")) {
                throw Exception("Response does not contain 'access_token'")
            }

            val newAccessToken = json.getString("access_token")
            tokenManager.saveAccessToken(newAccessToken)
            newAccessToken
        } catch (e: Exception) {
            throw Exception("Failed to refresh token", e)
        }
    }

    private fun createRequestBody(): RequestBody {
        val refreshToken = tokenManager.getRefreshToken()
        val json = JSONObject().apply {
            put("refresh_token", refreshToken)
        }
        return json.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }
}