package net.dungeonhub.model.cnt_request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;

import javax.annotation.Nullable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class CntRequestCreationModel implements CreationModel {
    private long messageId;
    private Long user;
    @Nullable
    private Long claimer;
    private Instant time;
    private String coinValue;
    private String description;
    private String requirement;

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}