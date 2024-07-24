package ru.porcupine.chattask.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _countryCode = MutableLiveData<String>()
    val countryCode: LiveData<String> get() = _countryCode

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun setCountryCode(selectedCountryCodeWithPlus: String?) {
        _countryCode.value = selectedCountryCodeWithPlus!!
    }
}
