package net.dungeonhub.model.warning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WarningEvidenceCreationModel implements CreationModel {
    private String evidence;
    private long submitter;

    public String toJson() {
        return DungeonHubService.getInstance().getGson().toJson(this);
    }
}