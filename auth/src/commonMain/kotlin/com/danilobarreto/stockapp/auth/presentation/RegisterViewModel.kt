package com.danilobarreto.stockapp.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danilobarreto.stockapp.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RegisterUiState {
    data object Idle: RegisterUiState
    data object Loading: RegisterUiState
    data object Success: RegisterUiState
    data class Error(val message: String): RegisterUiState
}

class RegisterViewModel(
    private val repository: AuthRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String){
        if (name.isBlank() || email.isBlank() || password.isBlank()) return

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            _uiState.value = repository.register(name, email, password).fold(
                onSuccess = { RegisterUiState.Success },
                onFailure = { RegisterUiState.Error(it.message ?: "Error creating user") }
            )
        }
    }
}