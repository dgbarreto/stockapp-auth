package com.danilobarreto.stockapp.auth.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.ForgotPasswordScreen
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.auth.presentation.NewPasswordScreen
import com.danilobarreto.stockapp.auth.presentation.PasswordResetViewModel
import com.danilobarreto.stockapp.auth.presentation.RegisterScreen
import com.danilobarreto.stockapp.auth.presentation.RegisterViewModel
import com.danilobarreto.stockapp.auth.presentation.ResetCodeScreen
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private enum class SampleScreen {
    Login, Register, ForgotPassword, ResetCode, NewPassword
}

@Composable
fun SampleApp() {
    var screen by remember { mutableStateOf(SampleScreen.Login) }

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
    val passwordResetViewModel = remember { PasswordResetViewModel(repository) }

    StockAppTheme {
        when (screen) {
            SampleScreen.Login -> LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { /* sample isolado: sem próxima tela ainda */ },
                onNavigateToRegister = { screen = SampleScreen.Register },
                onForgotPassword = { screen = SampleScreen.ForgotPassword },
            )

            SampleScreen.Register -> RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = { screen = SampleScreen.Login },
                onNavigateToLogin = { screen = SampleScreen.Login },
            )

            SampleScreen.ForgotPassword -> ForgotPasswordScreen(
                viewModel = passwordResetViewModel,
                onBack = { screen = SampleScreen.Login },
                onCodeSent = { screen = SampleScreen.ResetCode },
            )

            SampleScreen.ResetCode -> ResetCodeScreen(
                viewModel = passwordResetViewModel,
                onBack = { screen = SampleScreen.ForgotPassword },
                onCodeValidated = { screen = SampleScreen.NewPassword },
            )

            SampleScreen.NewPassword -> NewPasswordScreen(
                viewModel = passwordResetViewModel,
                onBack = { screen = SampleScreen.ResetCode },
                onPasswordReset = { screen = SampleScreen.Login },
            )
        }
    }
}