package net.dungeonhub.model.static_message

/**
 * An ordered entry in a static message.
 *
 * The sealed variants prevent layout entries from being mistaken for linked objects. After checking that an entry is
 * a [LinkedObject], its [LinkedObject.value] is always available as a non-null object id.
 */
sealed interface StaticMessageObject {
    data class LinkedObject(
        val type: StaticMessageObjectType,
        val value: Long
    ) : StaticMessageObject

    data object Separator : StaticMessageObject

    companion object {
        fun linkedObject(type: StaticMessageObjectType, value: Long): LinkedObject = LinkedObject(type, value)
        fun ticketPanel(value: Long): LinkedObject = linkedObject(StaticMessageObjectType.TicketPanel, value)
        fun carryType(value: Long): LinkedObject = linkedObject(StaticMessageObjectType.CarryType, value)
        fun carryTier(value: Long): LinkedObject = linkedObject(StaticMessageObjectType.CarryTier, value)
        fun separator(): Separator = Separator
    }
}
