package net.dungeonhub.client

import net.dungeonhub.auth.AuthenticationProvider
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request

class AuthenticatedClient(val authenticationProvider: AuthenticationProvider) : DungeonHubClient() {
    override fun getApiRequest(httpUrl: HttpUrl): Request.Builder {
        val mediaType: MediaType = "multipart/form-data; boundary=---011000010111000001101001".toMediaType()

        return Request.Builder()
            .url(httpUrl)
            .addHeader("Content-Type", mediaType.toString())
            .addHeader(AUTHORIZATION, "Bearer " + authenticationProvider.apiToken)
    }

    companion object {
        private const val AUTHORIZATION: String = "Authorization"
    }
}