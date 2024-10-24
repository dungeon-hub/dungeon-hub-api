package net.dungeonhub.model.carry_tier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarryTierUpdateModel implements UpdateModel<CarryTierModel> {
    private Long category;
    private Long priceChannel;
    private String descriptiveName;
    private String thumbnailUrl;
    private String priceTitle;
    private String priceDescription;
    private String displayName;

    public static CarryTierUpdateModel fromCarryTier(CarryTierModel carryTier) {
        return new CarryTierUpdateModel(carryTier.getCategory().orElse(null),
                carryTier.getPriceChannel().orElse(null),
                carryTier.getActualDescriptiveName().orElse(null),
                carryTier.getThumbnailUrl().orElse(null), carryTier.getActualPriceTitle().orElse(null),
                carryTier.getPriceDescription().orElse(null), carryTier.getDisplayName());
    }

    public static CarryTierUpdateModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, CarryTierUpdateModel.class);
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }

    @Override
    public CarryTierModel apply(CarryTierModel model) {
        if (category != null) {
            model.setCategory(category);
        }

        if (priceChannel != null) {
            model.setPriceChannel(priceChannel);
        }

        if (descriptiveName != null) {
            model.setDescriptiveName(descriptiveName);
        }

        if (thumbnailUrl != null) {
            model.setThumbnailUrl(thumbnailUrl);
        }

        if (priceTitle != null) {
            model.setPriceTitle(priceTitle);
        }

        if (priceDescription != null) {
            model.setPriceDescription(priceDescription);
        }

        if (displayName != null) {
            model.setDisplayName(displayName);
        }

        return model;
    }
}