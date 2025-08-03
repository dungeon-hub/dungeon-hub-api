package net.dungeonhub.model.reputation

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class ReputationLeaderboardModel(
    val page: Int,
    val totalPages: Int,
    val reputation: List<ReputationSumModel>,
    val playerPosition: Int? = null,
    val playerReputation: ReputationSumModel? = null
) : Model {
    fun hasNextPage(): Boolean {
        return page < totalPages - 1
    }

    fun hasPrevPage(): Boolean {
        return page > 0
    }

    companion object {
        fun fromJson(json: String): ReputationLeaderboardModel {
            return MoshiService.moshi.adapter(ReputationLeaderboardModel::class.java).fromJson(json)!!
        }
    }
}