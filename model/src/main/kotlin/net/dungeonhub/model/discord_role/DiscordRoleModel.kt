package net.dungeonhub.model.discord_role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.server.DiscordServerModel;

@Getter
@Setter
@AllArgsConstructor
public class DiscordRoleModel implements Model {
    private long id;
    private String nameSchema;
    private boolean verifiedRole;
    private DiscordServerModel discordServerModel;

    public static DiscordRoleModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, DiscordRoleModel.class);
    }
}