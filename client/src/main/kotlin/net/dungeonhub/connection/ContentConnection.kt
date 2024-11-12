package net.dungeonhub.connection

import net.dungeonhub.structure.Connection
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object ContentConnection : Connection {
    private val apiUrl: HttpUrl.Builder
        get() = (DungeonHubConnection.apiUrl + "cdn/").toHttpUrl().newBuilder()

    fun getApiUrl(uri: String): HttpUrl.Builder {
        return (DungeonHubConnection.apiUrl + "cdn/" + uri).toHttpUrl().newBuilder()
    }

    fun getStaticUrl(uri: String): HttpUrl.Builder {
        val prefix = DungeonHubConnection.staticUrl
        if (prefix.isNullOrBlank()) {
            return getCdnUrl("static/$uri")
        }

        return (prefix + uri).toHttpUrl().newBuilder()
    }

    fun getCdnUrl(uri: String): HttpUrl.Builder {
        var prefix = DungeonHubConnection.cdnUrl
        if (prefix.isNullOrBlank()) {
            prefix = DungeonHubConnection.apiUrl + "cdn/"
        }

        return (prefix + uri).toHttpUrl().newBuilder()
    }

    private fun performUpload(data: ByteArray, url: HttpUrl): String? {
        val requestBody: RequestBody = data.toRequestBody("application/octet-stream".toMediaType())

        val request: Request = DungeonHubConnection.getApiRequest(url)
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

        return DungeonHubConnection.executeRequest(request)
    }
}