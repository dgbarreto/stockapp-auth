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

@Serializable
data class ForgotPasswordRequestDto(
    val email: String
)

@Serializable
data class ValidateResetCodeRequestDto(
    val email: String,
    val code: String
)

@Serializable
data class ValidateResetCodeResponseDto(
    val resetToken: String
)

@Serializable
data class ResetPasswordRequestDto(
    val resetToken: String,
    val newPassword: String
)

@Serializable
data class MessageResponseDto(
    val message: String
)

@Serializable
data class UserProfileDto(
    val name: String,
    val email: String,
    val createdAt: String
)