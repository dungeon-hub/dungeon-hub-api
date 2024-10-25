package net.dungeonhub.model.warning

import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.structure.model.Model

class WarningEvidenceModel(
    val id: Long,
    val warningModel: WarningModel,
    val evidence: String,
    val submitter: DiscordUserModel
) : Model