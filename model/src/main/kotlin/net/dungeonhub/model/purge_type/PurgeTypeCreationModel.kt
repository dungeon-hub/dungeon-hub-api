package net.dungeonhub.model.purge_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PurgeTypeCreationModel implements CreationModel {
    private String identifier;
    private String displayName;

    public PurgeTypeCreationModel setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public PurgeTypeCreationModel setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}