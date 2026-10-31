package net.dungeonhub.enums

// the server stores the ordinal, so existing entries must not be reordered or removed and new entries go at the end
enum class TicketPhase {
    Open,
    Closed;

    companion object {
        fun of(state: TicketState): TicketPhase = when (state) {
            TicketState.Creating, TicketState.Open -> Open
            TicketState.Closed, TicketState.Deleted -> Closed
        }
    }
}
