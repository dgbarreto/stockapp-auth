package com.danilobarreto.stockapp.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onMinhasOrdens: () -> Unit,
    onImportacoes: () -> Unit,
    onValuation: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize().background(StockAppColors.surface1)) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Column(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                        .padding(16.dp),
                ) {
                    Text("Perfil", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)
                    StockAppErrorBanner(state.message, modifier = Modifier.padding(top = 24.dp))
                }
            }
            is ProfileUiState.Success -> {
                val profile = state.profile

                // Cabeçalho colorido: mesmo ajuste de inset já feito no Valuation/Ordens —
                // .windowInsetsPadding(safeDrawing top-only) em vez de .safeContentPadding(),
                // que reserva um respiro horizontal extra em telas com navegação por gestos.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StockAppColors.primary, shape = StockAppShapes.headerBottomRadius)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 28.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    StockAppAvatar(
                        imageUrl = null,
                        fallbackText = profile.name.take(1).uppercase(),
                        fallbackBackgroundColor = StockAppColors.onPrimary.copy(alpha = 0.2f),
                        fallbackTextColor = StockAppColors.onPrimary,
                        size = 56.dp,
                    )
                    Column {
                        Text(profile.name, style = StockAppTypography.titleMedium, color = StockAppColors.onPrimary)
                        Text(
                            profile.email,
                            style = StockAppTypography.bodySmall,
                            color = StockAppColors.onPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp, bottom = 24.dp),
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            label = "Na Bufunfa+ desde",
                            value = formatMemberSince(profile.memberSinceIso),
                        )
                        // "Ordens lançadas" fica de fora por enquanto — o ProfileViewModel só
                        // conhece o AuthRepository, não o OrdersRepository (mesma separação de
                        // domínios do ValuationMappers.kt). Dá pra ligar isso quando o
                        // composition root (AppNavHost/App.kt) já estiver injetando ambos aqui.
                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            label = "Ordens lançadas",
                            value = profile.ordersCount.toString(),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(StockAppColors.surface2, shape = StockAppShapes.cardRadiusLarge)
                            .padding(vertical = 4.dp),
                    ) {
                        ProfileMenuRow("Minhas ordens", onClick = onMinhasOrdens, showTopDivider = false)
                        ProfileMenuRow("Importações da B3", onClick = onImportacoes)
                        ProfileMenuRow("Preço-teto e valuation", onClick = onValuation)
                        // Ainda não existem no app — mesmo placeholder já usado no Detalhe do
                        // ativo (ver AppNavHost.kt, onAlert).
                        ProfileMenuRow("Alertas de preço", onClick = {}, enabled = false)
                        ProfileMenuRow("Notificações", onClick = {}, enabled = false)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clickable(onClick = onLogout)
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            StockAppIcons.LogOut,
                            contentDescription = null,
                            tint = StockAppColors.textSecondary,
                            modifier = Modifier.height(18.dp),
                        )
                        Text(
                            "Sair da conta",
                            style = StockAppTypography.bodyMedium,
                            color = StockAppColors.textSecondary,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }

                    Text(
                        "Bufunfa+ · feito no Brasil",
                        style = StockAppTypography.labelSmall,
                        color = StockAppColors.textMuted,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(StockAppColors.surface2, shape = StockAppShapes.cardRadius).padding(15.dp),
    ) {
        Text(label, style = StockAppTypography.labelSmall, color = StockAppColors.textMuted)
        Text(value, style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun ProfileMenuRow(
    text: String,
    onClick: () -> Unit,
    showTopDivider: Boolean = true,
    enabled: Boolean = true,
) {
    Column {
        if (showTopDivider) {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(StockAppColors.divider))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text,
                style = StockAppTypography.bodyMedium,
                color = if (enabled) StockAppColors.textPrimary else StockAppColors.textMuted,
            )
            Icon(
                StockAppIcons.ChevronRight,
                contentDescription = null,
                tint = StockAppColors.textMuted,
                modifier = Modifier.height(18.dp),
            )
        }
    }
}

private val monthAbbreviations = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")

private fun formatMemberSince(iso: String): String {
    val year = iso.take(4)
    val month = iso.drop(5).take(2).toIntOrNull()
    val monthLabel = month?.let { monthAbbreviations.getOrNull(it - 1) }
    return if (monthLabel != null) "$monthLabel $year" else year
}