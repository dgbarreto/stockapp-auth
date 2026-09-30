package com.danilobarreto.stockapp.auth.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock

private const val KEY_ACCESS_TOKEN = "access_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_EXPIRES_AT = "access_token_expires_at"

class TokenStorage(private val settings: Settings = Settings()) {

    private val _hasSession = MutableStateFlow(readAccessToken() != null)

    /** Reativo: qualquer save()/clear(), venha de onde vier, propaga pra quem observa. */
    val hasSession: StateFlow<Boolean> = _hasSession.asStateFlow()

    fun save(accessToken: String, refreshToken: String) {
        settings.putString(KEY_ACCESS_TOKEN, accessToken)
        settings.putString(KEY_REFRESH_TOKEN, refreshToken)

        val claims = decodeJwtClaims(accessToken)
        if (claims != null) {
            val expiresAt = Clock.System.now().toEpochMilliseconds() + claims.lifetimeSeconds * 1000
            settings.putLong(KEY_EXPIRES_AT, expiresAt)
        } else {
            settings.remove(KEY_EXPIRES_AT)
        }
        _hasSession.value = true
    }

    fun readAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS_TOKEN)

    fun readRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH_TOKEN)

    fun readExpiresAtMillis(): Long? = settings.getLongOrNull(KEY_EXPIRES_AT)

    fun readClaims(): JwtClaims? = readAccessToken()?.let(::decodeJwtClaims)

    fun clear() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_EXPIRES_AT)
        _hasSession.value = false
    }
}