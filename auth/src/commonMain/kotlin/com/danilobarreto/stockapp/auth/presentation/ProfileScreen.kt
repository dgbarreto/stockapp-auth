package com.danilobarreto.stockapp.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppAvatar
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .padding(16.dp)
    ) {
        Text("Perfil", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)

        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                StockAppErrorBanner(state.message, modifier = Modifier.padding(top = 24.dp))
            }
            is ProfileUiState.Success -> {
                val profile = state.profile
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .background(StockAppColors.surface2, shape = StockAppShapes.cardRadiusLarge)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    StockAppAvatar(
                        imageUrl = null,
                        fallbackText = profile.name.take(1).uppercase(),
                        fallbackBackgroundColor = StockAppColors.primaryTint,
                        fallbackTextColor = StockAppColors.primary,
                        size = 64.dp,
                    )
                    Text(
                        profile.name,
                        style = StockAppTypography.titleMedium,
                        color = StockAppColors.textPrimary,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    Text(
                        profile.email,
                        style = StockAppTypography.bodySmall,
                        color = StockAppColors.textSecondary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Text(
                        "Membro desde ${profile.memberSinceIso.take(4)}",
                        style = StockAppTypography.labelSmall,
                        color = StockAppColors.textMuted,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StockAppColors.bgDanger,
                contentColor = StockAppColors.textDanger,
            ),
        ) {
            Text("Sair", style = StockAppTypography.buttonLabel)
        }
    }
}