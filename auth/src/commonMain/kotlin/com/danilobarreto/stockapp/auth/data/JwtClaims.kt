package com.danilobarreto.stockapp.auth.data

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class JwtClaims(
    val sub: String,
    val email: String? = null,
    val iat: Long,
    val exp: Long,
) {
    /** Tempo de vida em segundos, calculado pelo servidor — imune a relógio desalinhado. */
    val lifetimeSeconds: Long get() = exp - iat
}

private val jwtJson = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalEncodingApi::class)
private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

/**
 * Lê o payload SEM validar assinatura. Uso exclusivo pra UX (expiração, dados exibidos) —
 * autorização de verdade é sempre do backend/gateway.
 */
@OptIn(ExperimentalEncodingApi::class)
fun decodeJwtClaims(token: String): JwtClaims? = runCatching {
    val payload = token.split('.')[1]
    jwtJson.decodeFromString<JwtClaims>(base64Url.decode(payload).decodeToString())
}.getOrNull()