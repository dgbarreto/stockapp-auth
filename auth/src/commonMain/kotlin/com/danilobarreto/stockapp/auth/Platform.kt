package com.danilobarreto.stockapp.auth

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
