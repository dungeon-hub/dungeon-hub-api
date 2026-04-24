package net.dungeonhub.connection

import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.content.ByteArrayContent
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.structure.AuthenticatedConnection
import net.dungeonhub.structure.ClientlessConnection

class ContentConnection(override val client: DungeonHubClient) : AuthenticatedConnection(client) {
    private val apiUrl: Url
        get() = Url(DungeonHubClient.apiUrl + "cdn/")

    fun getApiUrl(uri: String): Url {
        return Url(DungeonHubClient.apiUrl + "cdn/" + uri)
    }

    fun getStaticUrl(uri: String): Url {
        val prefix = DungeonHubClient.staticUrl
        if (prefix.isNullOrBlank()) {
            return getCdnUrl("static/$uri")
        }

        return Url(prefix + uri)
    }

    fun getCdnUrl(uri: String): Url {
        var prefix = DungeonHubClient.cdnUrl
        if (prefix.isNullOrBlank()) {
            prefix = DungeonHubClient.apiUrl + "cdn/"
        }

        return Url(prefix + uri)
    }

    private suspend fun performUpload(data: ByteArray, url: Url): String? {
        return client.executeRawRequest {
            client.setupRequest(this)
            url(url)
            method = HttpMethod.Post
            setBody(ByteArrayContent(data, ContentType.Application.OctetStream))
        }?.bodyAsText()
    }

    suspend fun uploadFile(data: ByteArray, fileName: String): String? {
        val url = getApiUrl(fileName)

        return performUpload(data, url)
    }

    suspend fun uploadFile(data: ByteArray): String? {
        return performUpload(data, this.apiUrl)
    }

    suspend fun downloadFile(uri: String): String? {
        val url = getApiUrl(uri)

        return client.executeRawRequest {
            url(url)
            method = HttpMethod.Get
        }?.bodyAsText()
    }

    companion object : ClientlessConnection<ContentConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): ContentConnection {
            return ContentConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}