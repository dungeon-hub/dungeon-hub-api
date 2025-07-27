package net.dungeonhub.auth

import com.squareup.moshi.Json
import net.dungeonhub.auth.AuthenticationCredentials.authLoginUrl
import net.dungeonhub.auth.AuthenticationCredentials.clientId
import net.dungeonhub.auth.AuthenticationCredentials.clientSecret
import net.dungeonhub.model.auth.JwtTokenModel
import net.dungeonhub.providers.HttpClientProvider.httpClient
import net.dungeonhub.service.MoshiService
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.ResponseBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.Instant

object AuthenticationConnection : AuthenticationProvider {
    val logger: Logger = LoggerFactory.getLogger(AuthenticationConnection::class.java)

    private var jwtToken: JwtTokenModel = loadToken()

    override val apiToken: String
        @Synchronized
        get() {
            if (jwtToken.validUntil.isBefore(Instant.now())) {
                jwtToken = loadToken()
            }
            return jwtToken.token
        }

    @Synchronized
    fun loadToken(): JwtTokenModel {
        val requestBody: FormBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", clientId!!)
            .add("client_secret", clientSecret!!)
            .build()

        val request: Request = Request.Builder()
            .url(authLoginUrl!!)
            .post(requestBody)
            .build()

        val responseBody = try {
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.let { body: ResponseBody ->
                        try {
                            body.bytes()
                        } catch (_: IOException) {
                            null
                        }
                    }?.let { bytes: ByteArray? ->
                        String(
                            bytes!!, StandardCharsets.UTF_8
                        )
                    }
                } else {
                    null
                }
            }
        } catch (ioException: IOException) {
            logger.error(null, ioException)
            null
        } ?: throw IllegalStateException("Could not load token")

        val response = MoshiService.moshi.adapter(LoginResponse::class.java).fromJson(responseBody)!!

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