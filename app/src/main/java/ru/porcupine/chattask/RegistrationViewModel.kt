package ru.porcupine.chattask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.porcupine.chattask.network.ApiService
import ru.porcupine.chattask.network.RegisterRequest
import ru.porcupine.chattask.network.RetrofitInstance

class RegistrationViewModel : ViewModel() {

    private val apiService: ApiService = RetrofitInstance.api

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
                        saveTokens(it.refreshToken, it.accessToken)
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

    private fun saveTokens(refreshToken: String, accessToken: String) {
        // Save tokens in shared preferences or any secure storage
    }
}
