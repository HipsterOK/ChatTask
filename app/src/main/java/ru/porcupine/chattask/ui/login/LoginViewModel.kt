package ru.porcupine.chattask.ui.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.porcupine.chattask.R
import ru.porcupine.chattask.data.model.CodeRequest
import ru.porcupine.chattask.data.model.PhoneRequest
import ru.porcupine.chattask.data.network.ApiService
import ru.porcupine.chattask.data.network.RetrofitInstance
import ru.porcupine.chattask.data.repository.UserRepository
import ru.porcupine.chattask.domain.usecase.CurrentRegionUseCase
import ru.porcupine.chattask.util.TokenManager

class LoginViewModel(
    application: Application, private val currentRegionUseCase: CurrentRegionUseCase
) : AndroidViewModel(application) {

    private val _currentRegion = MutableLiveData<String>()
    val currentRegion: LiveData<String> get() = _currentRegion

    private val _codeVisibility = MutableLiveData<Boolean>()
    val codeVisibility: LiveData<Boolean> get() = _codeVisibility

    private val _navigateDict = MutableLiveData<Int>()
    val navigateDict: LiveData<Int> get() = _navigateDict

    private val apiService: ApiService = RetrofitInstance.api
    private val tokenManager: TokenManager = TokenManager(application)

    init {
        loadCurrentRegion()
        _codeVisibility.value = false
        _navigateDict.value = 0
    }

    private fun loadCurrentRegion() {
        val region: String = currentRegionUseCase.getCurrentRegion()
        _currentRegion.value = region
    }

    fun onClickLoginBtn(phoneNumber: String, code: String?) {
        if (!codeVisibility.value!!) {
            sendAuthCode(phoneNumber)
        } else {
            code?.let { checkAuthCode(phoneNumber, it) }
        }
    }

    private fun sendAuthCode(phoneNumber: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.sendAuthCode(PhoneRequest(phoneNumber))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.is_success == true) {
                        _codeVisibility.value = true
                    } else {
                        val errorMessage =
                            response.errorBody()?.string() ?: "Не удалось отправить код"
                        showToast(errorMessage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Произошла ошибка при отправке кода: ${e.message}")
                }
            }
        }
    }

    private fun checkAuthCode(phoneNumber: String, code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = CodeRequest(phoneNumber, code)
                val response = apiService.checkAuthCode(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.isUserExists) {
                                saveTokens(it.refreshToken.toString(), it.accessToken.toString())
                                val userRepository =
                                    UserRepository(getApplication<Application>().applicationContext)
                                if (userRepository.lastUserID() != it.userId) {
                                    it.userId?.let { it1 -> userRepository.setLastUserID(it1) }
                                    userRepository.setShouldLoadFromServer(true)
                                }
                                goToChatsLists()
                            } else {
                                goToRegister()
                            }
                        }
                    } else {
                        val errorMessage =
                            response.errorBody()?.string() ?: "Не удалось проверить код"
                        showToast(errorMessage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Произошла ошибка при проверке кода: ${e.message}")
                }
            }
        }
    }

    private fun saveTokens(refreshToken: String, accessToken: String) {
        tokenManager.saveRefreshToken(refreshToken)
        tokenManager.saveAccessToken(accessToken)
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show()
        }
    }

    fun goToRegister() {
        _navigateDict.value = R.id.action_loginFragment_to_registrationFragment
    }

    private fun goToChatsLists() {
        _navigateDict.value = R.id.action_loginFragment_to_main_graph
    }

    fun resetDictionary() {
        _navigateDict.value = 0
    }

    fun backToPhone() {
        _codeVisibility.value = false
    }
}
