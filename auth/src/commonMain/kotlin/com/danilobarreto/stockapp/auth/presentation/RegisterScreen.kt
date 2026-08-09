package com.danilobarreto.stockapp.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
){
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        viewModel.reset()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Criar conta",
                style = StockAppTypography.titleLarge,
                color = StockAppColors.textPrimary
            )
            Text(
                "Leva menos de um minuto",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textSecondary,
            )
        }

        StockAppTextField(
            label = "Nome",
            value = name,
            onValueChange = { name = it },
            placeholder = "Seu nome",
        )

        StockAppTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "seu@email.com",
            keyboardType = KeyboardType.Email,
        )

        StockAppTextField(
            label = "Senha",
            value = password,
            onValueChange = { password = it },
            placeholder = "Mínimo de 8 caracteres",
            isPassword = true,
        )

        if (uiState is RegisterUiState.Error) {
            StockAppErrorBanner((uiState as RegisterUiState.Error).message)
        }

        StockAppPrimaryButton(
            text = "Criar conta",
            onClick = { viewModel.register(name, email, password, onSuccess = onRegisterSuccess) },
            loading = uiState is RegisterUiState.Loading,
            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
        )

        val footerText = buildAnnotatedString {
            append("Já tem conta? ")
            withStyle(
                SpanStyle(
                    color = StockAppColors.textAccent,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("Entrar")
            }
        }
        Text(
            footerText,
            style = StockAppTypography.bodyMedium,
            color = StockAppColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToLogin() },
        )
    }
}
