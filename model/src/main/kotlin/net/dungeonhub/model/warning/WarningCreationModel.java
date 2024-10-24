package net.dungeonhub.model.warning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;
import me.taubsie.dungeonhub.common.enums.WarningType;

@AllArgsConstructor
@Getter
public class WarningCreationModel implements CreationModel {
    private long user;
    private long striker;
    private WarningType warningType;
    private String reason;
    private boolean active;

    public String toJson() {
        return DungeonHubService.getInstance().getGson().toJson(this);
    }
}