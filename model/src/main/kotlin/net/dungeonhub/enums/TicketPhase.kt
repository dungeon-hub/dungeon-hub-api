package net.dungeonhub.enums

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
