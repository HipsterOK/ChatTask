package ru.porcupine.chattask.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.porcupine.chattask.util.TokenManager
import ru.porcupine.chattask.util.TokenRefreshManager

object RetrofitInstance {
    private const val BASE_URL = "https://plannerok.ru"
    private var initialized = false
    private lateinit var tokenManager: TokenManager
    private lateinit var tokenRefreshManager: TokenRefreshManager

    private val client: OkHttpClient
        get() {
            checkInitialization()
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(AuthInterceptor(tokenManager, tokenRefreshManager))
                .build()
        }

    private val retrofit: Retrofit
        get() {
            checkInitialization()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    val api: ApiService
        get() {
            checkInitialization()
            return retrofit.create(ApiService::class.java)
        }

    fun initialize(tokenManager: TokenManager, tokenRefreshManager: TokenRefreshManager) {
        if (initialized) {
            Log.d("RetrofitInstance", "Already initialized")
            return
        }
        this.tokenManager = tokenManager
        this.tokenRefreshManager = tokenRefreshManager
        initialized = true
        Log.d("RetrofitInstance", "Initialized with $tokenManager and $tokenRefreshManager")
    }

    private fun checkInitialization() {
        if (!initialized) {
            Log.e("RetrofitInstance", "RetrofitInstance not initialized")
            throw IllegalStateException("RetrofitInstance is not initialized")
        }
    }
}
