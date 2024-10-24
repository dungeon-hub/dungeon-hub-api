package net.dungeonhub.model.score;

import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.carry.CarryModel;

import java.util.List;

public record LoggedCarryModel(CarryModel carryModel, List<ScoreModel> scoreModels) implements Model {
    public static LoggedCarryModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, LoggedCarryModel.class);
    }
}