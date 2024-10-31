package net.dungeonhub.model.score;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScoreUpdateModel {
    private long id;
    private long amount;

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}