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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography

@Composable
fun ForgotPasswordScreen(
    viewModel: PasswordResetViewModel,
    onBack: () -> Unit,
    onCodeSent: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
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
            Text("Esqueci minha senha", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)
            Text(
                "Digite seu e-mail e enviaremos um código de confirmação.",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textSecondary,
            )
        }

        StockAppTextField(
            label = "E-mail",
            value = email,
            onValueChange = { email = it },
            placeholder = "seu@email.com",
            keyboardType = KeyboardType.Email,
        )

        if (uiState is PasswordResetUiState.Error) {
            StockAppErrorBanner((uiState as PasswordResetUiState.Error).message)
        }

        StockAppPrimaryButton(
            text = "Enviar código",
            onClick = { viewModel.requestCode(email, onSuccess = onCodeSent) },
            loading = uiState is PasswordResetUiState.Loading,
            enabled = email.isNotBlank(),
        )
    }
}