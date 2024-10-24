package net.dungeonhub.model.warning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.enums.WarningType;
import me.taubsie.dungeonhub.common.model.discord_user.DiscordUserModel;
import me.taubsie.dungeonhub.common.model.server.DiscordServerModel;

import javax.annotation.Nullable;
import java.time.Instant;

@AllArgsConstructor
@Getter
public class WarningModel implements Model {
    private long id;
    private DiscordServerModel server;
    private DiscordUserModel user;
    private DiscordUserModel striker;
    @Setter
    private WarningType warningType;
    @Nullable
    @Setter
    private String reason;
    @Setter
    private boolean active;
    private Instant time;

    public static WarningModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, WarningModel.class);
    }
}