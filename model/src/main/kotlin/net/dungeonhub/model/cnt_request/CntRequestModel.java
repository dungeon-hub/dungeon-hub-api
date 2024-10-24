package net.dungeonhub.model.cnt_request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.discord_user.DiscordUserModel;
import me.taubsie.dungeonhub.common.model.server.DiscordServerModel;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class CntRequestModel implements Model {
    private long id;
    private long messageId;
    private DiscordServerModel discordServer;
    private DiscordUserModel user;
    private DiscordUserModel claimer;
    private Instant time;
    private String coinValue;
    private String description;
    private String requirement;

    public static CntRequestModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, CntRequestModel.class);
    }
}