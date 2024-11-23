package net.dungeonhub.connection

import com.squareup.moshi.adapter
import net.dungeonhub.model.carry_difficulty.CarryDifficultyCreationModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyModel
import net.dungeonhub.model.carry_difficulty.CarryDifficultyUpdateModel
import net.dungeonhub.model.carry_tier.CarryTierModel
import net.dungeonhub.service.MoshiService.moshi
import net.dungeonhub.structure.ModuleConnection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@OptIn(ExperimentalStdlibApi::class)
class CarryDifficultyConnection(carryTierModel: CarryTierModel) : ModuleConnection {
    override val moduleApiPrefix = ("server/"
            + carryTierModel.carryType.server.id
            + "/carry-type/"
            + carryTierModel.carryType.id
            + "/carry-tier/"
            + carryTierModel.id
            + "/carry-difficulty")

    fun getCarryDifficulty(id: Long): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .get()
            .build()

        return executeRequest(request) { json: String -> CarryDifficultyModel.fromJson(json) }
    }

    val allCarryDifficulties: List<CarryDifficultyModel>?
        get() {
            val url: HttpUrl = getApiUrl("all").build()

            val request: Request = getApiRequest(url)
                .get()
                .build()

            return executeRequest(request, function = moshi.adapter<List<CarryDifficultyModel>>()::fromJson)
        }

    fun getByIdentifier(identifier: String?): CarryDifficultyModel? {
        return allCarryDifficulties?.firstOrNull {
            it.identifier.equals(
                identifier,
                ignoreCase = true
            )
        }
    }

    fun createCarryDifficulty(creationModel: CarryDifficultyCreationModel): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl().build()

        val requestBody = creationModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .post(requestBody)
            .build()

        return executeRequest(request) { CarryDifficultyModel.fromJson(it) }
    }

    fun updateCarryDifficulty(id: Long, updateModel: CarryDifficultyUpdateModel): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val requestBody: RequestBody = updateModel.toJson().toRequestBody(jsonMediaType)

        val request: Request = getApiRequest(url)
            .put(requestBody)
            .build()

        return executeRequest(request) { json: String -> CarryDifficultyModel.fromJson(json) }
    }

    fun deleteCarryDifficulty(id: Long): CarryDifficultyModel? {
        val url: HttpUrl = getApiUrl(id).build()

        val request: Request = getApiRequest(url)
            .delete()
            .build()

        return executeRequest(request) { CarryDifficultyModel.fromJson(it) }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CarryDifficultyConnection::class.java)
        private val instances: MutableMap<CarryTierModel, CarryDifficultyConnection> = HashMap()

        operator fun get(carryTierModel: CarryTierModel): CarryDifficultyConnection {
            return instances.computeIfAbsent(carryTierModel) { CarryDifficultyConnection(it) }
        }
    }
}