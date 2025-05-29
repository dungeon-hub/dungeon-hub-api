package net.dungeonhub.client

import net.dungeonhub.providers.HttpClientProvider.httpClient
import net.dungeonhub.structure.MappingFunction
import net.dungeonhub.structure.RequestResult
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okio.Buffer
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

open class DungeonHubClient {
    private val logger = LoggerFactory.getLogger(DungeonHubClient::class.java)

    fun <T> executeRequest(request: Request, notFoundFallback: T? = null, function: MappingFunction<String, T>): T? {
        val result = executeRawRequest(request)?.stringResult

        if (result?.code == 404 && result.result == null) {
            return notFoundFallback
        }

        return result?.result?.let { function.apply(it) }
    }

    fun executeRawRequest(request: Request): RequestResult? {
        try {
            httpClient.newCall(request).execute().use { response ->
                val bytes = response.body?.let {
                    try {
                        it.bytes()
                    } catch (ioException: IOException) {
                        logger.error(null, ioException)
                        null
                    }
                }

                if (response.isSuccessful) {
                    logger.debug("Executed request to '{}' successfully.", request.url)
                } else if (response.code == 404) {
                    logger.debug("Executed request to '{}' returned a 404.", request.url)
                } else {
                    val body = getBody(request)

                    logger.error(
                        "Request to '{}' wasn't successful. Body:\n{}\nResponse: {}\n{}",
                        request.url,
                        body,
                        response.code,
                        if (response.body != null) bytes?.let { String(it, StandardCharsets.UTF_8) } else null
                    )
                }

                return RequestResult(response.code, if (bytes?.isEmpty() != false) null else bytes)
            }
        } catch (ioException: IOException) {
            logger.error(null, ioException)
            return null
        }
    }

    fun executeRequest(request: Request): String? {
        return executeRawRequest(request)?.stringResult?.successResult
    }

    private fun getBody(request: Request): String? {
        val newRequest = request.newBuilder().build()

        if (newRequest.body == null) {
            return null
        }

        try {
            Buffer().use { buffer ->
                newRequest.body!!.writeTo(buffer)
                return buffer.readUtf8()
            }
        } catch (_: IOException) {
            return null
        } catch (_: NullPointerException) {
            return null
        }
    }

    fun getApiRequest(uri: String): Request.Builder {
        return getApiRequest(getApiUrl(uri).build())
    }

    open fun getApiRequest(httpUrl: HttpUrl): Request.Builder {
        val mediaType: MediaType = "multipart/form-data; boundary=---011000010111000001101001".toMediaType()

        return Request.Builder()
            .url(httpUrl)
            .addHeader("Content-Type", mediaType.toString())
    }

    fun getApiUrl(uri: String): HttpUrl.Builder {
        return (apiUrl + API_PREFIX + uri).toHttpUrl().newBuilder()
    }

    companion object {
        private const val API_PREFIX: String = "api/v1/"

        var apiUrl: String? = System.getenv("DHAPI_URL")
        var cdnUrl: String? = System.getenv("DHAPI_CDN_URL")
        var staticUrl: String? = System.getenv("DHAPI_STATIC_URL")
    }
}