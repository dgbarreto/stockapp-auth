package com.danilobarreto.stockapp.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilobarreto.stockapp.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PasswordResetUiState {
    data object Idle : PasswordResetUiState
    data object Loading : PasswordResetUiState
    data class Error(val message: String) : PasswordResetUiState
}

class PasswordResetViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<PasswordResetUiState>(PasswordResetUiState.Idle)
    val uiState: StateFlow<PasswordResetUiState> = _uiState.asStateFlow()

    private var email: String = ""
    private var resetToken: String? = null

    fun reset() {
        _uiState.value = PasswordResetUiState.Idle
    }

    fun requestCode(email: String, onSuccess: () -> Unit) {
        if (email.isBlank()) return
        this.email = email

        viewModelScope.launch {
            _uiState.value = PasswordResetUiState.Loading
            repository.requestPasswordReset(email).fold(
                onSuccess = {
                    _uiState.value = PasswordResetUiState.Idle
                    onSuccess()
                },
                onFailure = { _uiState.value = PasswordResetUiState.Error(it.message ?: "Erro ao enviar código") }
            )
        }
    }

    fun validateCode(code: String, onSuccess: () -> Unit) {
        if (code.isBlank()) return

        viewModelScope.launch {
            _uiState.value = PasswordResetUiState.Loading
            repository.validateResetCode(email, code).fold(
                onSuccess = { token ->
                    resetToken = token
                    onSuccess()
                },
                onFailure = { _uiState.value = PasswordResetUiState.Error(it.message ?: "Código inválido") }
            )
        }
    }

    fun resetPassword(newPassword: String, onSuccess: () -> Unit) {
        val token = resetToken ?: return
        if (newPassword.isBlank()) return

        viewModelScope.launch {
            _uiState.value = PasswordResetUiState.Loading
            repository.resetPassword(token, newPassword).fold(
                onSuccess = { onSuccess() },
                onFailure = { _uiState.value = PasswordResetUiState.Error(it.message ?: "Erro ao trocar senha") }
            )
        }
    }

    fun resendCode() {
        if (email.isBlank()) return
        requestCode(email) { /* fica na mesma tela, só reenvia */ }
    }
}