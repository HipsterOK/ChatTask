package ru.porcupine.chattask.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.porcupine.chattask.domain.usecase.CurrentRegionUseCase

class LoginViewModelFactory(
    private val application: Application,
    private val currentRegionUseCase: CurrentRegionUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(application, currentRegionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
