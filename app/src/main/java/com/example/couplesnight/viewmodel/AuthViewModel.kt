package com.example.couplesnight.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.couplesnight.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginStatus = MutableLiveData<Boolean>().apply { value = false }
    val loginStatus: LiveData<Boolean> get() = _loginStatus

    private val _registerStatus = MutableLiveData<Boolean>().apply { value = false }
    val registerStatus: LiveData<Boolean> get() = _registerStatus

    fun login(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = authRepository.loginUser(email, password)
            _loginStatus.postValue(result)
        }
    }

    fun register(email: String, password: String, displayName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = authRepository.registerUser(email, password, displayName)
            _registerStatus.postValue(result)
        }
    }
}
