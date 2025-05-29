package net.dungeonhub.structure

import net.dungeonhub.auth.AuthenticationConnection
import net.dungeonhub.auth.AuthenticationProvider

interface ClientlessConnection<T> {
    fun authenticated(): T {
        return authenticated(AuthenticationConnection)
    }

    fun authenticated(authenticationProvider: AuthenticationProvider): T
}