package net.dungeonhub.model.carry_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CarryTypeCreationModel implements CreationModel {
    private String identifier;
    private String displayName;
    private Long logChannel;
    private Long leaderboardChannel;
    private Boolean eventActive;

    public CarryTypeCreationModel(String identifier, String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
    }

    public CarryTypeCreationModel setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public CarryTypeCreationModel setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public CarryTypeCreationModel setLogChannel(Long logChannel) {
        this.logChannel = logChannel;
        return this;
    }

    public CarryTypeCreationModel setLeaderboardChannel(Long leaderboardChannel) {
        this.leaderboardChannel = leaderboardChannel;
        return this;
    }

    public CarryTypeCreationModel setEventActive(Boolean eventActive) {
        this.eventActive = eventActive;
        return this;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}