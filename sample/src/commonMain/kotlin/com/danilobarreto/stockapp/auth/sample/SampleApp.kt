package com.danilobarreto.stockapp.auth.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.auth.presentation.RegisterScreen
import com.danilobarreto.stockapp.auth.presentation.RegisterViewModel
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Composable
fun SampleApp() {
    var showRegister by remember { mutableStateOf(false) }

    val repository = remember {
        val httpClient = HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val apiClient = AuthApiClient(httpClient, baseUrl = sampleBaseUrl())
        AuthRepositoryImpl(apiClient, TokenStorage())
    }
    val loginViewModel = remember { LoginViewModel(repository) }
    val registerViewModel = remember { RegisterViewModel(repository) }

    StockAppTheme {
        if (showRegister) {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = { showRegister = false },
                onNavigateToLogin = { showRegister = false },
            )
        } else {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { /* sample isolado: sem próxima tela ainda */ },
                onNavigateToRegister = { showRegister = true },
            )
        }
    }
}
