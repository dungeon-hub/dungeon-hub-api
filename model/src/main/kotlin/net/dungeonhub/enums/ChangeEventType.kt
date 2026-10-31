package net.dungeonhub.enums

import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_queue.CarryQueueModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.model.carry_type.CarryTypeModel
import net.dungeonhub.model.reputation.ReputationModel
import net.dungeonhub.model.score.ScoreModel
import net.dungeonhub.model.static_message.StaticMessageModel
import net.dungeonhub.model.ticket.TicketModel
import net.dungeonhub.model.ticket_panel.TicketPanelModel
import net.dungeonhub.structure.model.Model

/**
 * All entity types whose changes are propagated through the change stream.
 * The name of the entry is used as the event name on the wire, the [modelClass] is the type of the
 * [net.dungeonhub.model.change.ChangeEventModel.data] that is sent along with the event.
 *
 * Clients should ignore event names they don't know, so that new entries can be added without breaking older clients.
 */
enum class ChangeEventType(val modelClass: Class<out Model>) {
    Ticket(TicketModel::class.java),
    StaticMessage(StaticMessageModel::class.java),
    CarryQueue(CarryQueueModel::class.java),
    CarryType(CarryTypeModel::class.java),
    CarryTier(CarryTierModel::class.java),
    CarryDifficulty(CarryDifficultyModel::class.java),
    TicketPanel(TicketPanelModel::class.java),
    Score(ScoreModel::class.java),
    Reputation(ReputationModel::class.java);

    companion object {
        fun fromEventName(eventName: String?): ChangeEventType? {
            return entries.firstOrNull { it.name == eventName }
        }
    }
}
