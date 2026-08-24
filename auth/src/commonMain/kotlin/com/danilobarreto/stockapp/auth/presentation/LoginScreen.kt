package com.danilobarreto.stockapp.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.primary),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .safeContentPadding()
                .padding(horizontal = 26.dp, vertical = 30.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(StockAppColors.onPrimary, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "b+",
                    style = StockAppTypography.titleLarge.copy(fontSize = 26.sp),
                    color = StockAppColors.primary,
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "bufunfa+",
                style = StockAppTypography.displayLarge.copy(fontSize = 44.sp),
                color = StockAppColors.onPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sua bufunfa, no controle. Carteira, ordens e proventos num só lugar.",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.onPrimary.copy(alpha = 0.86f),
                modifier = Modifier.widthIn(max = 270.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(StockAppColors.surface1, shape = StockAppShapes.sheetTopRadius)
                .padding(horizontal = 26.dp)
                .padding(top = 30.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            StockAppTextField(
                label = "E-mail",
                value = email,
                onValueChange = { email = it },
                placeholder = "seu@email.com",
                keyboardType = KeyboardType.Email,
            )

            StockAppTextField(
                label = "Senha",
                value = password,
                onValueChange = { password = it },
                placeholder = "Sua senha",
                isPassword = true,
            )

            if (uiState is LoginUiState.Error) {
                StockAppErrorBanner((uiState as LoginUiState.Error).message)
            }

            StockAppPrimaryButton(
                text = "Entrar",
                onClick = { viewModel.login(email, password, onSuccess = onLoginSuccess) },
                loading = uiState is LoginUiState.Loading,
                enabled = email.isNotBlank() && password.isNotBlank(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Criar conta",
                    style = StockAppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = StockAppColors.primaryDeep,
                    modifier = Modifier.clickable { onNavigateToRegister() },
                )
                Text(
                    "Esqueci a senha",
                    style = StockAppTypography.bodyMedium,
                    color = StockAppColors.textSecondary,
                    modifier = Modifier.clickable { onForgotPassword() },
                )
            }
        }
    }
}