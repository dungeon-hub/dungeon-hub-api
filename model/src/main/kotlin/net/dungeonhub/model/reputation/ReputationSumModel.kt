package net.dungeonhub.model.reputation

import net.dungeonhub.model.discord_user.DiscordUserModel

class ReputationSumModel(
    val user: DiscordUserModel,
    val amount: Long
)