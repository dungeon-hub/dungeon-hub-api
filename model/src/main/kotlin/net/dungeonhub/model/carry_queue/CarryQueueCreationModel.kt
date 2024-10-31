package net.dungeonhub.model.carry_queue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;
import me.taubsie.dungeonhub.common.enums.QueueStep;

import java.time.Instant;

@NoArgsConstructor
@Getter
public class CarryQueueCreationModel implements CreationModel {
    private QueueStep queueStep;
    private Long carrier;
    private Long player;
    private Long amount;
    private Long relationId;
    private String attachmentLink;
    private Instant time;

    public CarryQueueCreationModel setQueueStep(QueueStep queueStep) {
        this.queueStep = queueStep;
        return this;
    }

    public CarryQueueCreationModel setCarrier(Long carrier) {
        this.carrier = carrier;
        return this;
    }

    public CarryQueueCreationModel setPlayer(Long player) {
        this.player = player;
        return this;
    }

    public CarryQueueCreationModel setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public CarryQueueCreationModel setRelationId(Long relationId) {
        this.relationId = relationId;
        return this;
    }

    public CarryQueueCreationModel setAttachmentLink(String attachmentLink) {
        this.attachmentLink = attachmentLink;
        return this;
    }

    public CarryQueueCreationModel setTime(Instant time) {
        this.time = time;
        return this;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}
