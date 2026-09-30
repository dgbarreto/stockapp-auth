package com.danilobarreto.stockapp.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

private const val REFRESH_MARGIN_MILLIS = 60_000L

class SessionManager(
    private val tokenStorage: TokenStorage,
    private val baseUrl: String,
    private val refreshClient: HttpClient,
    private val clock: Clock = Clock.System,
) {
    private val mutex = Mutex()

    /** Token pronto pra uso — renova antes se estiver a menos de 60s de expirar. */
    suspend fun validAccessToken(): String? = mutex.withLock {
        val token = tokenStorage.readAccessToken() ?: return@withLock null
        if (isFresh()) token else refreshLocked()
    }

    /** Fallback do 401. Se outro request já renovou enquanto este esperava o lock, só reaproveita. */
    suspend fun refreshAfterUnauthorized(failedToken: String): String? = mutex.withLock {
        val current = tokenStorage.readAccessToken() ?: return@withLock null
        if (current != failedToken) current else refreshLocked()
    }

    private fun isFresh(): Boolean {
        val expiresAt = tokenStorage.readExpiresAtMillis() ?: return false
        return clock.now().toEpochMilliseconds() < expiresAt - REFRESH_MARGIN_MILLIS
    }

    private suspend fun refreshLocked(): String? {
        val refreshToken = tokenStorage.readRefreshToken()
        if (refreshToken == null) {
            tokenStorage.clear()
            return null
        }
        return try {
            val response: AuthResponseDto = refreshClient.post("$baseUrl/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequestDto(refreshToken))
            }.body()
            tokenStorage.save(response.accessToken, response.refreshToken)
            response.accessToken
        } catch (e: ClientRequestException) {
            // 4xx do /auth/refresh: refresh expirado/revogado → sessão acabou de verdade.
            tokenStorage.clear()
            null
        }
        // Erro de rede / 5xx NÃO é capturado: sobe pro request original, sessão preservada.
    }
}