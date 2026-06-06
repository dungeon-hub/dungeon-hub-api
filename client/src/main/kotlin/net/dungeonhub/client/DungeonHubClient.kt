package net.dungeonhub.client

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.userAgent
import net.dungeonhub.providers.HttpClientProvider.httpClient
import net.dungeonhub.structure.ModuleConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

open class DungeonHubClient {
    val logger: Logger = LoggerFactory.getLogger(DungeonHubClient::class.java)

    suspend fun executeRawRequest(request: suspend HttpRequestBuilder.() -> Unit): HttpResponse? {
        try {
            val builder = HttpRequestBuilder()
            request(builder)

            httpClient.request(builder).let { response ->
                if(response.status.isSuccess()) {
                    logger.debug("Executed request to '{}' successfully.", response.request.url)
                } else if(response.status == HttpStatusCode.NotFound) {
                    logger.debug("Executed request to '{}' returned a 404.", response.request.url)
                } else {
                    val body = response.request.content
                    val bodyText = if(response.contentLength() == 0L || response.contentLength() == null) {
                        null
                    } else {
                        response.bodyAsText()
                    }

                    logger.error(
                        "Request to '{}' wasn't successful. Body:\n{}\nResponse: {} ({})\n{}",
                        response.request.url,
                        body,
                        response.status.value,
                        response.status.description,
                        bodyText
                    )
                }

                return response
            }
        } catch (ioException: IOException) {
            logger.error(null, ioException)
            return null
        }
    }

    suspend fun executeModuleRequest(uri: String, request: HttpRequestBuilder.() -> Unit, module: ModuleConnection? = null): HttpResponse? {
        try {
            return executeRawRequest {
                url(module?.getApiUrl(uri) ?: getApiUrl(uri))
                setupRequest(this)
                request()
            }
        } catch (exception: Exception) {
            logger.error("Exception during API request.", exception)
            return null
        }
    }

    suspend inline fun <reified T> dhApiRequest(uri: String, noinline request: HttpRequestBuilder.() -> Unit, module: ModuleConnection? = null): T? {
        try {
            val response = executeModuleRequest(uri, request, module)
            if(response?.status?.isSuccess() != true) return null
            return response.body<T>()
        } catch (exception: Exception) {
            logger.error("Exception during API request.", exception)
            return null
        }
    }

    suspend inline fun <reified T> dhApiRequest(uri: Long, noinline request: HttpRequestBuilder.() -> Unit, module: ModuleConnection? = null): T? = dhApiRequest(uri.toString(), request, module)

    suspend inline fun <reified T> dhApiRequest(noinline request: HttpRequestBuilder.() -> Unit, module: ModuleConnection? = null): T? = dhApiRequest("", request, module)

    open suspend fun setupRequest(requestBuilder: HttpRequestBuilder) {
        requestBuilder.userAgent("DHApiModule")
        requestBuilder.contentType(ContentType.Application.Json)
    }

    open fun getApiUrl(uri: String): Url {
        return Url(apiUrl + API_PREFIX + uri) // TODO add checks and exceptions in case apiUrl is not set
    }

    companion object {
        private const val API_PREFIX: String = "api/v1/"

        var apiUrl: String? = System.getenv("DHAPI_URL")
        var cdnUrl: String? = System.getenv("DHAPI_CDN_URL")
        var staticUrl: String? = System.getenv("DHAPI_STATIC_URL")
    }
}