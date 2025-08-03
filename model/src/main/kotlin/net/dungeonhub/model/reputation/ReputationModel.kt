package net.dungeonhub.model.reputation

import net.dungeonhub.model.cnt_request.CntRequestModel
import net.dungeonhub.model.discord_user.DiscordUserModel
import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.UpdateableModel
import java.time.Instant

class ReputationModel(
    val id: Long,
    val user: DiscordUserModel,
    val reputor: DiscordUserModel,
    val cntRequest: CntRequestModel?,
    val amount: Int,
    val reason: String?,
    val active: Boolean,
    val time: Instant
): UpdateableModel<ReputationUpdateModel, ReputationModel> {
    override fun getUpdateModel(): ReputationUpdateModel {
        return ReputationUpdateModel(null, null, null)
    }

    companion object {
        fun fromJson(json: String): ReputationModel {
            return MoshiService.moshi.adapter(ReputationModel::class.java).fromJson(json)!!
        }
    }
}