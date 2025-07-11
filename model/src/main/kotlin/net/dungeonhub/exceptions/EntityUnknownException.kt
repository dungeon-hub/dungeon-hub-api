package net.dungeonhub.exceptions

class EntityUnknownException(
    val id: Long
) : IllegalStateException()