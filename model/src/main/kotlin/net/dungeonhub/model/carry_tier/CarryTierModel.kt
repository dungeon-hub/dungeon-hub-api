package net.dungeonhub.model.carry_tier;

import lombok.*;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.carry_type.CarryTypeModel;

import java.util.Optional;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
@Setter
public class CarryTierModel implements Model {
    private Long category;
    private Long priceChannel;
    private String descriptiveName;
    private String thumbnailUrl;
    private String priceTitle;
    private String priceDescription;
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private long id;
    @Setter(AccessLevel.NONE)
    private String identifier;
    private String displayName;
    private CarryTypeModel carryType;

    public static CarryTierModel fromJson(String json) {
        return DungeonHubService.getInstance().getGson().fromJson(json, CarryTierModel.class);
    }

    public String getDescriptiveName() {
        return getActualDescriptiveName().orElse(getDisplayName());
    }

    public Optional<String> getActualDescriptiveName() {
        return Optional.ofNullable(descriptiveName == null || descriptiveName.isBlank() ? null : descriptiveName);
    }

    public Optional<Long> getCategory() {
        return Optional.ofNullable(category > 0L ? category : null);
    }

    public long getActualCategory() {
        return category;
    }

    public Optional<String> getThumbnailUrl() {
        return Optional.ofNullable(thumbnailUrl == null || thumbnailUrl.isBlank() ? null : thumbnailUrl);
    }

    public String getActualThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPriceTitle() {
        return getActualPriceTitle().orElse(getDescriptiveName());
    }

    public Optional<String> getActualPriceTitle() {
        return Optional.ofNullable(priceTitle == null || priceTitle.isBlank() ? null : priceTitle);
    }

    public Optional<String> getPriceDescription() {
        return Optional.ofNullable(priceDescription == null || priceDescription.isBlank() ? null : priceDescription);
    }

    public String getActualPriceDescription() {
        return priceDescription;
    }

    public Optional<Long> getPriceChannel() {
        return Optional.ofNullable(priceChannel != null && priceChannel > 0L ? priceChannel : null);
    }

    public Long getActualPriceChannel() {
        return priceChannel;
    }

    public String toJson() {
        return DungeonHubService.getInstance().getGson().toJson(this);
    }
}