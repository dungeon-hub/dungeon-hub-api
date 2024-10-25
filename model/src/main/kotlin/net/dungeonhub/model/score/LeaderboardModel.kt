package net.dungeonhub.model.score

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class LeaderboardModel(
    val page: Int,
    val totalPages: Int,
    val scores: List<ScoreModel>,
    val playerPosition: Int? = null,
    val playerScore: ScoreModel? = null
) : Model {
    fun hasNextPage(): Boolean {
        return page < totalPages - 1
    }

    fun hasPrevPage(): Boolean {
        return page > 0
    }

    companion object {
        fun fromJson(json: String): LeaderboardModel {
            return MoshiService.moshi.adapter(LeaderboardModel::class.java).fromJson(json)!!
        }
    }
}