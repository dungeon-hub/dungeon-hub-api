package net.dungeonhub.enums

import dev.kordex.i18n.Key
import net.dungeonhub.model.static_message.StaticMessageObject
import net.dungeonhub.model.static_message.StaticMessageObjectType

enum class StaticMessageType(
    val linkedObjectType: StaticMessageObjectType?,
    val supportsSeparators: Boolean = false
) {
    ScoreLeaderboard(StaticMessageObjectType.CarryType),
    TotalLeaderboard(null),
    ReputationLeaderboard(null),
    TicketPanel(StaticMessageObjectType.TicketPanel, supportsSeparators = true),
    PriceMessage(StaticMessageObjectType.CarryTier);

    val readableName = Key(name.replace(Regex("([A-Z])"), " $1").trim())

    fun validateObjects(objects: List<StaticMessageObject>) {
        objects.forEachIndexed { index, entry ->
            when (entry) {
                is StaticMessageObject.LinkedObject -> require(entry.type == linkedObjectType) {
                    "$this only supports linked objects of type $linkedObjectType, but entry $index is ${entry.type}"
                }
                StaticMessageObject.Separator -> require(supportsSeparators) {
                    "$this does not support separators"
                }
            }
        }
    }
}
