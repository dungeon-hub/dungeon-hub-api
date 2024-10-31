package net.dungeonhub.model.carry_type;

import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;

@Getter
@Setter
public class CarryTypeUpdateModel implements UpdateModel<CarryTypeModel> {
    private String displayName;
    private Long logChannel;
    private Long leaderboardChannel;
    private Boolean eventActive;

    @Override
    public CarryTypeModel apply(CarryTypeModel carryTypeModel) {
        if(displayName != null) {
            carryTypeModel.setDisplayName(displayName);
        }

        if(logChannel != null) {
            carryTypeModel.setLogChannel(logChannel);
        }

        if(leaderboardChannel != null) {
            carryTypeModel.setLeaderboardChannel(leaderboardChannel);
        }

        if(eventActive != null) {
            carryTypeModel.setEventActive(eventActive);
        }

        return carryTypeModel;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}