package net.dungeonhub.enums

// the server stores this by ordinal, so new entries have to be added at the end
enum class TicketPermissionCandidate {
    SupportTeam,
    AdditionalRoles,
    TicketCreator,
    TicketClaimer,
    Everyone
}