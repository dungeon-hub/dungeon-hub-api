package net.dungeonhub.model.carry_queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.enums.QueueStep;
import me.taubsie.dungeonhub.common.model.carry_difficulty.CarryDifficultyModel;
import me.taubsie.dungeonhub.common.model.carry_tier.CarryTierModel;
import me.taubsie.dungeonhub.common.model.carry_type.CarryTypeModel;
import me.taubsie.dungeonhub.common.model.discord_user.DiscordUserModel;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class CarryQueueModel implements Model {
    private final long id;
    private QueueStep queueStep;
    private DiscordUserModel carrier;
    private DiscordUserModel player;
    private Long amount;
    private CarryDifficultyModel carryDifficulty;
    private Long relationId;
    private String attachmentLink;
    private Instant time;

    public static CarryQueueModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, CarryQueueModel.class);
    }

    public CarryTypeModel getCarryType() {
        return getCarryTier().getCarryType();
    }

    public CarryTierModel getCarryTier() {
        return getCarryDifficulty().getCarryTier();
    }

    public long calculateScore() {
        return getScoreMultiplier() * getAmount();
    }

    private long getScoreMultiplier() {
        return getCarryDifficulty().getScore();
    }
}