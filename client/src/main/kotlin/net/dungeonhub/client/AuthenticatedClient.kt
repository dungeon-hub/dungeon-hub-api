package net.dungeonhub.client

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import net.dungeonhub.auth.AuthenticationProvider

open class AuthenticatedClient(val authenticationProvider: AuthenticationProvider) : DungeonHubClient() {
    override suspend fun setupRequest(requestBuilder: HttpRequestBuilder) {
        super.setupRequest(requestBuilder)
        requestBuilder.header(AUTHORIZATION, "Bearer " + authenticationProvider.getApiToken())
    }

    companion object {
        private const val AUTHORIZATION: String = "Authorization"
    }
}