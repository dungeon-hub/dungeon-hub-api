package net.dungeonhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;

@Getter
@AllArgsConstructor
public class ScoreResetModel implements Model {
    private long defaultCount;
    private long eventCount;

    public static ScoreResetModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, ScoreResetModel.class);
    }
}