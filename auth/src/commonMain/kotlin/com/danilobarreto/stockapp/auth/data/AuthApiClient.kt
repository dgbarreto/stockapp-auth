package com.danilobarreto.stockapp.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.plugin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.auth.clearAuthTokens

class AuthApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
){
    suspend fun register(dto: RegisterRequestDto): AuthResponseDto =
        httpClient.post("$baseUrl/auth/register"){
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    suspend fun login(dto: LoginRequestDto): AuthResponseDto =
        httpClient.post("$baseUrl/auth/login"){
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    fun invalidateCachedToken() {
        httpClient.clearAuthTokens()
    }
}

suspend fun parseErrorMessage(exception: ClientRequestException): String {
    val bodyText = runCatching { exception.response.bodyAsText() }.getOrNull()
    return bodyText
        ?.let { runCatching { Json{ ignoreUnknownKeys = true }.decodeFromString<ErrorResponseDto>(it).message }.getOrNull() }
        ?: "Unable to complete the operation"
}
