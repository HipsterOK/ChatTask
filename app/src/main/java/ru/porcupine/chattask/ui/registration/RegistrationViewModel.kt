package ru.porcupine.chattask.ui.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.porcupine.chattask.data.model.RegisterRequest
import ru.porcupine.chattask.data.network.ApiService
import ru.porcupine.chattask.data.network.RetrofitInstance
import ru.porcupine.chattask.util.TokenManager

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitInstance.api
    private val tokenManager = TokenManager(application)

    fun registerUser(
        phone: String,
        name: String,
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isUsernameValid(username)) {
            onError("Некорректный username: можно использовать только заглавные и строчные латинские буквы, цифры, символы '-' и '_'")
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.registerUser(RegisterRequest(phone, name, username))
                if (response.isSuccessful) {
                    response.body()?.let {
                        val refreshToken = it.refreshToken
                        val accessToken = it.accessToken

                        tokenManager.saveRefreshToken(refreshToken)
                        tokenManager.saveAccessToken(accessToken)
                        onSuccess()
                    }
                } else {
                    onError(response.errorBody()?.string() ?: "Ошибка регистрации")
                }
            } catch (e: Exception) {
                onError("Произошла ошибка: ${e.message}")
            }
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        val regex = "^[A-Za-z0-9_-]+$".toRegex()
        return username.matches(regex)
    }
}