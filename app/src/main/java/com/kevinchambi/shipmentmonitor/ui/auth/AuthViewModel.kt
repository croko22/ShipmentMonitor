package com.kevinchambi.shipmentmonitor.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinchambi.shipmentmonitor.data.model.LoginData
import com.kevinchambi.shipmentmonitor.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<Result<LoginData>?>()
    val loginResult: LiveData<Result<LoginData>?> = _loginResult

    private val _forgotResult = MutableLiveData<Result<String>?>()
    val forgotResult: LiveData<Result<String>?> = _forgotResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _loginResult.value = result
            _isLoading.value = false
        }
    }

    fun forgotPassword(email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.forgotPassword(email)
            _forgotResult.value = result
            _isLoading.value = false
        }
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

    fun clearForgotResult() {
        _forgotResult.value = null
    }
}
