package net.dungeonhub.providers

import com.hypercubetools.ktor.moshi.moshi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import net.dungeonhub.service.MoshiService

object HttpClientProvider {
    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            moshi(MoshiService.moshi)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            retryOnExceptionIf { request, cause ->
                request.method in setOf(HttpMethod.Get, HttpMethod.Head, HttpMethod.Put, HttpMethod.Post, HttpMethod.Delete) &&
                        cause !is CancellationException &&
                        cause is IOException
            }
            exponentialDelay()
        }
    }
}