package net.dungeonhub.model

import net.dungeonhub.model.discord_user.DiscordUserCreationModel
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class DiscordUserCreationModelTest {
    @Test
    fun testCorrectSerializationOfUuid() {
        val discordUserCreationModel = DiscordUserCreationModel(
            0,
            UUID.fromString("39642ffc-a7fb-4d24-a1d4-916f4cad1d98")
        )

        val json = discordUserCreationModel.toJson()

        assertEquals("{\"id\":0,\"minecraftId\":\"39642ffc-a7fb-4d24-a1d4-916f4cad1d98\"}", json.replace("\\s".toRegex(), ""))
    }
}