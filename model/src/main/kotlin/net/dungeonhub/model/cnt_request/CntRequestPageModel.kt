package net.dungeonhub.model.cnt_request

import net.dungeonhub.service.MoshiService
import net.dungeonhub.structure.model.Model

class CntRequestPageModel(
    val page: Int,
    val totalPages: Int,
    val totalElements: Long,
    val requests: List<CntRequestModel>
) : Model {
    fun hasNextPage(): Boolean {
        return page < totalPages - 1
    }

    fun hasPrevPage(): Boolean {
        return page > 0
    }

    companion object {
        fun fromJson(json: String): CntRequestPageModel {
            return MoshiService.moshi.adapter(CntRequestPageModel::class.java).fromJson(json)!!
        }
    }
}
