package net.dungeonhub.auth

import com.squareup.moshi.Json
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.dungeonhub.auth.AuthenticationCredentials.authLoginUrl
import net.dungeonhub.auth.AuthenticationCredentials.clientId
import net.dungeonhub.auth.AuthenticationCredentials.clientSecret
import net.dungeonhub.model.auth.JwtTokenModel
import net.dungeonhub.providers.HttpClientProvider.httpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.Instant

object AuthenticationConnection : AuthenticationProvider {
    val logger: Logger = LoggerFactory.getLogger(AuthenticationConnection::class.java)

    private var jwtToken: JwtTokenModel = runBlocking { loadToken() }
    private val tokenMutex = Mutex()

    override suspend fun getApiToken(): String {
        return tokenMutex.withLock {
            val current = jwtToken
            if (current.validUntil.isBefore(Instant.now())) {
                jwtToken = loadToken()
            }
            jwtToken.token
        }
    }

    suspend fun loadToken(): JwtTokenModel {
        val response = try {
            httpClient.submitForm(
                url = authLoginUrl!!,
                formParameters = parameters {
                    append("grant_type", "client_credentials")
                    append("client_id", clientId!!)
                    append("client_secret", clientSecret!!)
                }
            ).let { response ->
                if(response.status.isSuccess()) {
                    response.body<LoginResponse>()
                } else {
                    logger.error("Could not load token (${response.status}): ${response.bodyAsText()}")
                    null
                }
            }
        } catch (ioException: IOException) {
            logger.error(null, ioException)
            null
        } ?: throw IllegalStateException("Could not load token")

        val token = response.accessToken
        val expiresIn = response.expiresIn
        val validUntil = Instant.now().plusSeconds(expiresIn.toLong())

        return JwtTokenModel(token, validUntil)
    }

    class LoginResponse(
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "expires_in") val expiresIn: Int
    )
}