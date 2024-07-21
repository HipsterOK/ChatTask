package ru.porcupine.chattask

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.porcupine.chattask.network.ApiService
import ru.porcupine.chattask.network.CodeRequest
import ru.porcupine.chattask.network.PhoneRequest
import ru.porcupine.chattask.network.RetrofitInstance

class LoginViewModel(private val currentRegionUseCase: CurrentRegionUseCase) : ViewModel() {

    private val _currentRegion = MutableLiveData<String>()
    var currentRegion: LiveData<String> = _currentRegion

    private val _codeVisibility = MutableLiveData<Boolean>()
    var codeVisibility: LiveData<Boolean> = _codeVisibility

    private val _loginSuccess = MutableLiveData<Boolean>()
    var loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _navigateDict = MutableLiveData<Int>()
    var navigateDict: LiveData<Int> = _navigateDict

    private val _phoneNumber = MutableLiveData<String>()
    var phoneNumber: LiveData<String> = _phoneNumber

    private val apiService: ApiService = RetrofitInstance.api

    init {
        loadCurrentRegion()
        _codeVisibility.value = false
        _loginSuccess.value = false
        _navigateDict.value = 0
    }

    private fun loadCurrentRegion() {
        val region: String = currentRegionUseCase.getCurrentRegion()
        _currentRegion.value = region
    }

    fun onClickLoginBtn(context: Context, phoneNumber: String, code: String?) {
        if (!codeVisibility.value!!) {
            sendAuthCode(context, phoneNumber)
        } else {
            code?.let { checkAuthCode(context, phoneNumber, it) }
        }
    }

    private fun sendAuthCode(context: Context, phoneNumber: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.sendAuthCode(PhoneRequest(phoneNumber))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.is_success == true) {
                        _phoneNumber.value = phoneNumber
                        _codeVisibility.value = true
                    } else {
                        val errorMessage =
                            response.errorBody()?.string() ?: "Не удалось отправить код"
                        showToast(context, errorMessage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast(context, "Произошла ошибка при отправке кода: ${e.message}")
                }
            }
        }
    }

    private fun checkAuthCode(context: Context, phoneNumber: String, code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.checkAuthCode(CodeRequest(phoneNumber, code))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            saveTokens(it.refreshToken, it.accessToken)
                            if (it.isUserExists) {
                                _loginSuccess.value = true
                                goToChat()
                            } else {
                                goToRegister()
                            }
                        }
                    } else {
                        val errorMessage =
                            response.errorBody()?.string() ?: "Не удалось проверить код"
                        showToast(context, errorMessage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast(context, "Произошла ошибка при проверке кода: ${e.message}")
                }
            }
        }
    }

    private fun goToChat() {

    }

    private fun saveTokens(refreshToken: String, accessToken: String) {
        // Save tokens in shared preferences or any secure storage
    }

    private suspend fun showToast(context: Context, message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun goToRegister() {
        _navigateDict.value = R.id.action_loginFragment_to_registrationFragment
    }

    fun resetDictionary() {
        _navigateDict.value = 0
    }
}
