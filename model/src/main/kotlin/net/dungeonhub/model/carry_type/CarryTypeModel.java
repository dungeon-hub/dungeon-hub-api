package net.dungeonhub.model.carry_type;

import lombok.*;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.server.DiscordServerModel;

import java.util.Optional;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
@Setter
public class CarryTypeModel implements Model {
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private long id;
    @Setter(AccessLevel.NONE)
    private String identifier;
    private String displayName;
    private DiscordServerModel server;
    private Long logChannel;
    private Long leaderboardChannel;
    private Boolean eventActive;

    public static CarryTypeModel fromJson(String json) {
        return DungeonHubService.getInstance().getGson().fromJson(json, CarryTypeModel.class);
    }

    public Long getActualLogChannel() {
        return logChannel;
    }

    public Long getActualLeaderboardChannel() {
        return leaderboardChannel;
    }

    public Optional<Long> getLogChannel() {
        return Optional.ofNullable(logChannel != null && logChannel > 0 ? logChannel : null);
    }

    public Optional<Long> getLeaderboardChannel() {
        return Optional.ofNullable(leaderboardChannel != null && leaderboardChannel > 0 ? leaderboardChannel : null);
    }

    public boolean isEventActive() {
        return Boolean.TRUE.equals(eventActive);
    }

    public String toJson() {
        return DungeonHubService.getInstance().getGson().toJson(this);
    }
}