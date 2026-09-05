package com.danilobarreto.stockapp.auth.domain

data class UserProfile(
    val name: String,
    val email: String,
    val memberSinceIso: String, // "createdAt" cru do backend, ISO-8601
)