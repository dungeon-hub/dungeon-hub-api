package net.dungeonhub.enums

// the server stores this by ordinal, so new entries have to be added at the end
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
