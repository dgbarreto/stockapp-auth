package com.danilobarreto.stockapp.auth.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.data.createAuthenticatedHttpClient
import com.danilobarreto.stockapp.auth.presentation.ForgotPasswordScreen
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.auth.presentation.NewPasswordScreen
import com.danilobarreto.stockapp.auth.presentation.PasswordResetViewModel
import com.danilobarreto.stockapp.auth.presentation.ProfileScreen
import com.danilobarreto.stockapp.auth.presentation.ProfileViewModel
import com.danilobarreto.stockapp.auth.presentation.RegisterScreen
import com.danilobarreto.stockapp.auth.presentation.RegisterViewModel
import com.danilobarreto.stockapp.auth.presentation.ResetCodeScreen
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.launch

private enum class SampleScreen {
    Login, Register, ForgotPassword, ResetCode, NewPassword, Profile
}

@Composable
fun SampleApp() {
    val tokenStorage = remember { TokenStorage() }
    val hasSession by tokenStorage.hasSession.collectAsState()

    // Já logado (sessão salva) → abre direto no Perfil, pra testar o refresh ao reabrir o app.
    var screen by remember {
        mutableStateOf(if (tokenStorage.hasSession.value) SampleScreen.Profile else SampleScreen.Login)
    }
    val coroutineScope = rememberCoroutineScope()

    // Sessão encerrada por fora (refresh falhou dentro do SessionManager) → volta pro Login.
    LaunchedEffect(hasSession) {
        if (!hasSession && screen == SampleScreen.Profile) {
            println("SAMPLE_AUTH → sessão encerrada, voltando pro Login")
            screen = SampleScreen.Login
        }
    }

    val repository = remember {
        val baseUrl = sampleBaseUrl()
        val httpClient = createAuthenticatedHttpClient(baseUrl, tokenStorage)
        httpClient.plugin(HttpSend).intercept { request ->
            val call = execute(request)
            println("SAMPLE_HTTP → ${request.method.value} ${request.url.buildString()} → ${call.response.status.value}")
            call
        }
        val apiClient = AuthApiClient(httpClient, baseUrl = baseUrl)
        AuthRepositoryImpl(apiClient, tokenStorage)
    }
    val loginViewModel = remember { LoginViewModel(repository) }
    val registerViewModel = remember { RegisterViewModel(repository) }
    val passwordResetViewModel = remember { PasswordResetViewModel(repository) }
    val profileViewModel = remember { ProfileViewModel(repository) }

    StockAppTheme {
        when (screen) {
            SampleScreen.Login -> LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { screen = SampleScreen.Profile },
                onNavigateToRegister = { screen = SampleScreen.Register },
                onForgotPassword = { screen = SampleScreen.ForgotPassword },
            )

            SampleScreen.Register -> RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = { screen = SampleScreen.Login },
                onNavigateToLogin = { screen = SampleScreen.Login },
                onBack = { screen = SampleScreen.Login },
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

            SampleScreen.Profile -> ProfileScreen(
                viewModel = profileViewModel,
                onLogout = {
                    coroutineScope.launch {
                        repository.logout()
                        screen = SampleScreen.Login
                    }
                },
                // Sem NavHost de verdade aqui — o sample só mostra que o clique dispara o
                // callback certo (println), a navegação real é coisa do stockapp-app.
                onMinhasOrdens = { println("SAMPLE_AUTH → Minhas ordens") },
                onImportacoes = { println("SAMPLE_AUTH → Importações da B3") },
                onValuation = { println("SAMPLE_AUTH → Preço-teto e valuation") },
            )
        }
    }
}
