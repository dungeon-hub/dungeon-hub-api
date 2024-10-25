package net.dungeonhub.model.auth

import java.time.Instant

@JvmRecord
data class JwtTokenModel(val token: String, val validUntil: Instant) 