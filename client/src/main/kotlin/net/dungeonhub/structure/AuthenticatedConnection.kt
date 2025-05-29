package net.dungeonhub.structure

import net.dungeonhub.auth.AuthenticationConnection
import net.dungeonhub.auth.AuthenticationProvider
import net.dungeonhub.client.AuthenticatedClient
import net.dungeonhub.client.DungeonHubClient

abstract class AuthenticatedConnection : Connection {
    constructor(client: DungeonHubClient)

    constructor(authenticationProvider: AuthenticationProvider): this(AuthenticatedClient(authenticationProvider))

    constructor() : this(AuthenticationConnection)
}