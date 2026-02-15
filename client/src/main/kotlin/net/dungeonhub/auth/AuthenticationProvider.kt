package net.dungeonhub.auth

interface AuthenticationProvider {
    suspend fun getApiToken(): String
}