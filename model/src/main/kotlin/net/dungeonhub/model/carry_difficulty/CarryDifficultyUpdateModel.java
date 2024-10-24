package net.dungeonhub.model.carry_difficulty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarryDifficultyUpdateModel implements UpdateModel<CarryDifficultyModel> {
    private String displayName;
    private String thumbnailUrl;
    private Integer bulkPrice;
    private Integer bulkAmount;
    private String priceName;
    private Integer price;
    private Integer score;

    @Override
    public CarryDifficultyModel apply(CarryDifficultyModel model) {
        if (displayName != null) {
            model.setDisplayName(displayName);
        }

        if (thumbnailUrl != null) {
            model.setThumbnailUrl(thumbnailUrl);
        }

        if (bulkPrice != null) {
            model.setBulkPrice(bulkPrice);
        }

        if (bulkAmount != null) {
            model.setBulkAmount(bulkAmount);
        }

        if (priceName != null) {
            model.setPriceName(priceName);
        }

        if (price != null) {
            model.setPrice(price);
        }

        if (score != null) {
            model.setScore(score);
        }

        return model;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}