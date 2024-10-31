package net.dungeonhub.model.carry_tier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarryTierCreationModel implements CreationModel {
    private String identifier;
    private String displayName;
    private Long category;
    private Long priceChannel;
    private String descriptiveName;
    private String thumbnailUrl;
    private String priceTitle;
    private String priceDescription;

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}