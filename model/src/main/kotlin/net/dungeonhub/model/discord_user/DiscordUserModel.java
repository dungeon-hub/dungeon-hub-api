package net.dungeonhub.model.discord_user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DiscordUserModel implements Model {
    private final long id;
    private UUID minecraftId;

    public static DiscordUserModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, DiscordUserModel.class);
    }
}