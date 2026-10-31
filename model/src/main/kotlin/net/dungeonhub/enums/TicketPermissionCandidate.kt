package net.dungeonhub.enums

// the server stores the ordinal, so existing entries must not be reordered or removed and new entries go at the end
enum class TicketPermissionCandidate {
    SupportTeam,
    AdditionalRoles,
    TicketCreator,
    TicketClaimer,
    Everyone
}