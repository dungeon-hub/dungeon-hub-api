package net.dungeonhub.auth

object AuthenticationCredentials {
    var authLoginUrl: String? = System.getenv("DHAPI_AUTH_LOGIN_URL")
    var clientId: String? = System.getenv("DHAPI_CLIENT_ID")
    var clientSecret: String? = System.getenv("DHAPI_CLIENT_SECRET")
}