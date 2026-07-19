package com.danilobarreto.stockapp.auth.data

import com.russhwolf.settings.Settings

private const val KEY_ACCESS_TOKEN = "access_token"

class TokenStorage(private val settings: Settings = Settings()){
    fun save(token: String){
        settings.putString(KEY_ACCESS_TOKEN, token)
    }

    fun read(): String? = settings.getStringOrNull(KEY_ACCESS_TOKEN)

    fun clear(){
        settings.remove(KEY_ACCESS_TOKEN)
    }
}