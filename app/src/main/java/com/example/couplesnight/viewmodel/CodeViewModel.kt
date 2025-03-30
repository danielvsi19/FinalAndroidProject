package com.example.couplesnight.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.couplesnight.model.Code
import com.example.couplesnight.repository.CodeRepository
import kotlinx.coroutines.launch
import java.util.UUID

class CodeViewModel(application: Application) : AndroidViewModel(application) {

    private val codeRepository: CodeRepository = CodeRepository(application)
    private val _generatedCode = MutableLiveData<String>()
    val generatedCode: LiveData<String> get() = _generatedCode
    private val _codeValidationResult = MutableLiveData<Boolean>()
    val codeValidationResult: LiveData<Boolean> get() = _codeValidationResult

    fun generateCode() {
        val code = UUID.randomUUID().toString().substring(0, 6)
        viewModelScope.launch {
            codeRepository.saveCodeLocally(Code(code))
            codeRepository.saveCodeRemotely(code)
            _generatedCode.postValue(code)
        }
    }

    fun validateCode(inputCode: String) {
        viewModelScope.launch {
            val isValid = codeRepository.validateCodeRemotely(inputCode)
            _codeValidationResult.postValue(isValid)
        }
    }
}