package net.dungeonhub.model;

import java.time.Instant;

public record JwtTokenModel(String token, Instant validUntil) {
}