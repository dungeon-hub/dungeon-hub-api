package net.dungeonhub.model.score;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.enums.ScoreType;
import me.taubsie.dungeonhub.common.model.carry_type.CarryTypeModel;
import me.taubsie.dungeonhub.common.model.discord_user.DiscordUserModel;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@Setter
public class ScoreModel implements Model {
    private DiscordUserModel carrier;
    @Nullable
    private CarryTypeModel carryType;
    private ScoreType scoreType;
    private Long scoreAmount;

    public static ScoreModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, ScoreModel.class);
    }
}