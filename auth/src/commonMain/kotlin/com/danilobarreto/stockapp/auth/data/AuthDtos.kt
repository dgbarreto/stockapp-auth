package com.danilobarreto.stockapp.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String
)

@Serializable
data class ErrorResponseDto(
    val statusCode: Int,
    val message: String,
    val error: String? = null
)