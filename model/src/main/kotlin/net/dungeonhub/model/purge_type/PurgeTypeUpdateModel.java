package net.dungeonhub.model.purge_type;

import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;

@Getter
@Setter
public class PurgeTypeUpdateModel implements UpdateModel<PurgeTypeModel> {
    private String displayName;

    @Override
    public PurgeTypeModel apply(PurgeTypeModel purgeTypeModel) {
        if (displayName != null) {
            purgeTypeModel.setDisplayName(displayName);
        }

        return purgeTypeModel;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}