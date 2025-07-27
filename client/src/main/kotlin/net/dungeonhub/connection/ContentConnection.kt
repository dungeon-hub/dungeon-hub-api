package net.dungeonhub.connection

import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient
import net.dungeonhub.structure.AuthenticatedConnection
import net.dungeonhub.structure.ClientlessConnection
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ContentConnection(override val client: DungeonHubClient) : AuthenticatedConnection() {
    private val apiUrl: HttpUrl.Builder
        get() = (DungeonHubClient.apiUrl + "cdn/").toHttpUrl().newBuilder()

    fun getApiUrl(uri: String): HttpUrl.Builder {
        return (DungeonHubClient.apiUrl + "cdn/" + uri).toHttpUrl().newBuilder()
    }

    fun getStaticUrl(uri: String): HttpUrl.Builder {
        val prefix = DungeonHubClient.staticUrl
        if (prefix.isNullOrBlank()) {
            return getCdnUrl("static/$uri")
        }

        return (prefix + uri).toHttpUrl().newBuilder()
    }

    fun getCdnUrl(uri: String): HttpUrl.Builder {
        var prefix = DungeonHubClient.cdnUrl
        if (prefix.isNullOrBlank()) {
            prefix = DungeonHubClient.apiUrl + "cdn/"
        }

        return (prefix + uri).toHttpUrl().newBuilder()
    }

    private fun performUpload(data: ByteArray, url: HttpUrl): String? {
        val requestBody: RequestBody = data.toRequestBody("application/octet-stream".toMediaType())

        val request: Request = client.getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request)
    }

    fun uploadFile(data: ByteArray, fileName: String): String? {
        val url: HttpUrl = getApiUrl(fileName).build()

        return performUpload(data, url)
    }

    fun uploadFile(data: ByteArray): String? {
        val url: HttpUrl = this.apiUrl.build()

        return performUpload(data, url)
    }

    fun downloadFile(uri: String): String? {
        val url: HttpUrl = getApiUrl(uri).build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        return client.executeRequest(request)
    }

    companion object : ClientlessConnection<ContentConnection> {
        override fun authenticated(authenticationProvider: AuthenticationProvider): ContentConnection {
            return ContentConnection(AuthenticatedClient(authenticationProvider))
        }
    }
}