package net.dungeonhub.expections

class EntityUnknownException(
    val id: Long
) : IllegalStateException()