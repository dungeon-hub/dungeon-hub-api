package net.dungeonhub.model.cnt_request

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class CntRequestLeaderboardModel(
    val page: Int,
    val totalPages: Int,
    val requests: List<CntRequestModel>
) : Model {
    fun hasNextPage(): Boolean {
        return page < totalPages - 1
    }

    fun hasPrevPage(): Boolean {
        return page > 0
    }

    companion object {
        fun fromJson(json: String): CntRequestLeaderboardModel {
            return MoshiService.moshi.adapter(CntRequestLeaderboardModel::class.java).fromJson(json)!!
        }
    }
}
