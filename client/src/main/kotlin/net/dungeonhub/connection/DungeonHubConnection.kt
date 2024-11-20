package net.dungeonhub.connection

import net.dungeonhub.structure.MappingFunction
import net.dungeonhub.structure.RequestResult
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.Duration

object DungeonHubConnection {
    private const val API_PREFIX: String = "api/v1/"
    private const val AUTHORIZATION: String = "Authorization"

    var apiUrl: String? = System.getenv("DHAPI_URL")
    var cdnUrl: String? = System.getenv("DHAPI_CDN_URL")
    var staticUrl: String? = System.getenv("DHAPI_STATIC_URL")

    var authLoginUrl: String? = System.getenv("DHAPI_AUTH_LOGIN_URL")
    var clientId: String? = System.getenv("DHAPI_CLIENT_ID")
    var clientSecret: String? = System.getenv("DHAPI_CLIENT_SECRET")

    private val logger = LoggerFactory.getLogger(DungeonHubConnection::class.java)

    val httpClient: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(Duration.ofSeconds(30))
        .readTimeout(Duration.ofSeconds(30))
        .callTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(30))
        .build()

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
        } catch (exception: IOException) {
            return null
        } catch (exception: NullPointerException) {
            return null
        }
    }

    fun getApiRequest(uri: String): Request.Builder {
        return getApiRequest(getApiUrl(uri).build())
    }

    fun getApiRequest(httpUrl: HttpUrl): Request.Builder {
        val mediaType: MediaType = "multipart/form-data; boundary=---011000010111000001101001".toMediaType()

        return Request.Builder()
            .url(httpUrl)
            .addHeader("Content-Type", mediaType.toString())
            .addHeader(AUTHORIZATION, "Bearer " + AuthorizationConnection.apiToken)
    }

    fun getApiUrl(uri: String): HttpUrl.Builder {
        return (apiUrl + API_PREFIX + uri).toHttpUrl().newBuilder()
    }
}