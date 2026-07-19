package com.danilobarreto.stockapp.auth.data

import com.danilobarreto.stockapp.auth.domain.AuthRepository
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val apiClient: AuthApiClient,
    private val tokenStorage: TokenStorage
): AuthRepository{
    private val _isLoggedIn = MutableStateFlow(tokenStorage.read() != null)
    override val isLoggedIn = _isLoggedIn.asStateFlow()

    override suspend fun register(name: String, email: String, password: String): Result<Unit> =
        runCatching {
            val response = apiClient.register(RegisterRequestDto(name, email, password))
            tokenStorage.save(response.accessToken)
            _isLoggedIn.value = true
        }.recoverCatching { throwable ->
            throw mapAuthError(throwable)
        }

    override suspend fun login(email: String, password: String): Result<Unit> =
        runCatching {
            val response = apiClient.login(LoginRequestDto(email, password))
            tokenStorage.save(response.accessToken)
            _isLoggedIn.value = true
        }.recoverCatching { throwable ->
            throw mapAuthError(throwable)
        }

    override suspend fun logout() {
        tokenStorage.clear()
        _isLoggedIn.value = false
    }

    private suspend fun mapAuthError(throwable: Throwable): Throwable =
        if(throwable is ClientRequestException){
            Exception(parseErrorMessage(throwable))
        } else {
            throwable
        }
}