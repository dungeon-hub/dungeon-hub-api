package net.dungeonhub.model.carry_queue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;
import me.taubsie.dungeonhub.common.enums.QueueStep;
import me.taubsie.dungeonhub.common.model.carry_difficulty.CarryDifficultyModel;
import me.taubsie.dungeonhub.common.model.discord_user.DiscordUserModel;

import java.time.Instant;

@NoArgsConstructor
public class CarryQueueUpdateModel implements UpdateModel<CarryQueueModel> {
    private QueueStep queueStep;
    private DiscordUserModel carrier;
    private DiscordUserModel player;
    private Long amount;
    private CarryDifficultyModel carryDifficulty;
    private Long relationId;
    private String attachmentLink;
    private Instant time;
    @Getter
    private Long approver;

    public CarryQueueUpdateModel setQueueStep(QueueStep queueStep) {
        this.queueStep = queueStep;
        return this;
    }

    public CarryQueueUpdateModel setCarrier(DiscordUserModel carrier) {
        this.carrier = carrier;
        return this;
    }

    public CarryQueueUpdateModel setPlayer(DiscordUserModel player) {
        this.player = player;
        return this;
    }

    public CarryQueueUpdateModel setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public CarryQueueUpdateModel setCarryDifficulty(CarryDifficultyModel carryDifficulty) {
        this.carryDifficulty = carryDifficulty;
        return this;
    }

    public CarryQueueUpdateModel setRelationId(Long relationId) {
        this.relationId = relationId;
        return this;
    }

    public CarryQueueUpdateModel setAttachmentLink(String attachmentLink) {
        this.attachmentLink = attachmentLink;
        return this;
    }

    public CarryQueueUpdateModel setTime(Instant time) {
        this.time = time;
        return this;
    }

    public CarryQueueUpdateModel setApprover(Long approver) {
        this.approver = approver;
        return this;
    }

    @Override
    public CarryQueueModel apply(CarryQueueModel carryQueueModel) {
        if (queueStep != null) {
            carryQueueModel.setQueueStep(queueStep);
        }

        if (carrier != null) {
            carryQueueModel.setCarrier(carrier);
        }

        if (player != null) {
            carryQueueModel.setPlayer(player);
        }

        if (amount != null) {
            carryQueueModel.setAmount(amount);
        }

        if (carryDifficulty != null) {
            carryQueueModel.setCarryDifficulty(carryDifficulty);
        }

        if (relationId != null) {
            carryQueueModel.setRelationId(relationId);
        }

        if (attachmentLink != null) {
            carryQueueModel.setAttachmentLink(attachmentLink);
        }

        if (time != null) {
            carryQueueModel.setTime(time);
        }

        return carryQueueModel;
    }

    public String toJson() {
        return DungeonHubService.getInstance().getGson().toJson(this);
    }
}
