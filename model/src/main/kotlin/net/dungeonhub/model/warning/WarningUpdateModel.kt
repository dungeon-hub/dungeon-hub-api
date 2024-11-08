package net.dungeonhub.model.warning

import net.dungeonhub.enums.WarningType
import net.dungeonhub.structure.model.UpdateModel

class WarningUpdateModel(
    var warningType: WarningType?,
    reason: String?,
    var active: Boolean?
) : UpdateModel<WarningModel> {
    var reason = reason
        set(value) {
            field = value
            resetReason = value == null
        }

    var resetReason = false
        private set
}