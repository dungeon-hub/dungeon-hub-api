package net.dungeonhub.enums

/**
 * Custom ticket actions that a [TicketPermissionCandidate] can be allowed to perform.
 * An action can only be performed in the [phases] it applies to.
 * The server stores this by ordinal, so new entries have to be added at the end.
 */
enum class TicketAction(val phases: Set<TicketPhase>) {
    Reopen(setOf(TicketPhase.Closed)),
    Close(setOf(TicketPhase.Open)),
    Claim(setOf(TicketPhase.Open)),
    Unclaim(setOf(TicketPhase.Open)),
    Delete(setOf(TicketPhase.Open, TicketPhase.Closed)),
    AddUser(setOf(TicketPhase.Open)),
    GenerateTranscript(setOf(TicketPhase.Open, TicketPhase.Closed));

    companion object {
        fun forPhase(phase: TicketPhase): List<TicketAction> = entries.filter { phase in it.phases }
    }
}
