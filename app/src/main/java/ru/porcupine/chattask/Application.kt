package ru.porcupine.chattask

import android.app.Application
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.porcupine.chattask.data.network.RetrofitInstance
import ru.porcupine.chattask.data.repository.UserRepository
import ru.porcupine.chattask.util.TokenManager
import ru.porcupine.chattask.util.TokenRefreshManager

class Application : Application() {

    val userRepository: UserRepository by lazy {
        UserRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Application", "onCreate called")

        val tokenManager = TokenManager(this)
        Log.d("Application", "TokenManager initialized")

        val tokenRefreshManager = TokenRefreshManager(tokenManager)
        Log.d("Application", "TokenRefreshManager initialized")

        RetrofitInstance.initialize(tokenManager, tokenRefreshManager)
        Log.d("Application", "RetrofitInstance initialized")

        AndroidThreeTen.init(this)
    }
}
