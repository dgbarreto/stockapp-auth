package com.danilobarreto.stockapp.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private fun HttpClientConfig<*>.stockAppDefaults() {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

/**
 * HttpClient já autenticado: injeta o Bearer em todo request, renova o token
 * antes de expirar e, se ainda assim vier 401, renova uma vez e refaz o request.
 */
fun createAuthenticatedHttpClient(
    baseUrl: String,
    tokenStorage: TokenStorage,
    configure: HttpClientConfig<*>.() -> Unit = {},
): HttpClient {
    val sessionManager = SessionManager(
        tokenStorage = tokenStorage,
        baseUrl = baseUrl,
        refreshClient = HttpClient { stockAppDefaults() }, // sem auth: evita recursão
    )

    val client = HttpClient {
        stockAppDefaults()
        configure()
    }

    client.plugin(HttpSend).intercept { request ->
        val token = sessionManager.validAccessToken()
        token?.let { request.headers[HttpHeaders.Authorization] = "Bearer $it" }

        val call = execute(request)
        if (call.response.status != HttpStatusCode.Unauthorized || token == null) {
            return@intercept call
        }

        val newToken = sessionManager.refreshAfterUnauthorized(token)
            ?: return@intercept call // refresh falhou → 401 segue, sessão já foi limpa
        request.headers[HttpHeaders.Authorization] = "Bearer $newToken"
        execute(request)
    }

    return client
}