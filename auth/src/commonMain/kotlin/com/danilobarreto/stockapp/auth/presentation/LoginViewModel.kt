package com.danilobarreto.stockapp.auth.presentation

import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danilobarreto.stockapp.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState{
    data object Idle: LoginUiState
    data object Loading: LoginUiState
    data class Error(val message: String): LoginUiState
}

class LoginViewModel(
    private val repository: AuthRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun reset(){
        _uiState.value = LoginUiState.Idle
    }

    fun login(email: String, password: String, onSuccess: () -> Unit){
        if(email.isBlank() || password.isBlank()) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            repository.login(email, password).fold(
                onSuccess = { onSuccess() },
                onFailure = { _uiState.value = LoginUiState.Error(it.message ?: "Login error") }
            )
        }
    }
}