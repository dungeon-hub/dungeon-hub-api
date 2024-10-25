package net.dungeonhub.model.warning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.taubsie.dungeonhub.common.DungeonHubService;

import java.util.List;

@AllArgsConstructor
@Getter
public class AddedWarningModel {
    private WarningModel warningModel;
    private List<WarningActionModel> warningActionModel;

    public static AddedWarningModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, AddedWarningModel.class);
    }
}