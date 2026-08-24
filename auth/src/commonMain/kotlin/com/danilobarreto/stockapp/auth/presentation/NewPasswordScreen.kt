package com.danilobarreto.stockapp.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography

@Composable
fun NewPasswordScreen(
    viewModel: PasswordResetViewModel,
    onBack: () -> Unit,
    onPasswordReset: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    val passwordsMatch = password.isNotBlank() && password == confirmPassword
    val passwordLongEnough = password.length >= 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(StockAppColors.surface2, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = StockAppIcons.ArrowLeft,
                    contentDescription = "Voltar",
                    tint = StockAppColors.textPrimary,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Nova senha", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)
            Text(
                "Escolha uma senha forte para sua conta.",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textSecondary,
            )
        }

        StockAppTextField(
            label = "Nova senha",
            value = password,
            onValueChange = { password = it },
            placeholder = "Mínimo de 8 caracteres",
            isPassword = true,
        )

        StockAppTextField(
            label = "Confirmar senha",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Repita a senha",
            isPassword = true,
            isError = confirmPassword.isNotBlank() && !passwordsMatch,
            supportingText = if (confirmPassword.isNotBlank() && !passwordsMatch) "As senhas não coincidem" else null,
        )

        if (uiState is PasswordResetUiState.Error) {
            StockAppErrorBanner((uiState as PasswordResetUiState.Error).message)
        }

        StockAppPrimaryButton(
            text = "Salvar nova senha",
            onClick = { viewModel.resetPassword(password, onSuccess = onPasswordReset) },
            loading = uiState is PasswordResetUiState.Loading,
            enabled = passwordsMatch && passwordLongEnough,
        )
    }
}