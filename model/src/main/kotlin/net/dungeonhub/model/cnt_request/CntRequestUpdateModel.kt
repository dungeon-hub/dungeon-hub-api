package net.dungeonhub.model.cnt_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;
import me.taubsie.dungeonhub.common.model.discord_user.DiscordUserModel;

import javax.annotation.Nullable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CntRequestUpdateModel implements UpdateModel<CntRequestModel> {
    //TODO discuss -> needs to be changable?
    //private long messageId;
    @Nullable
    private DiscordUserModel claimer;
    private boolean removeClaimer;
    @Nullable
    private String coinValue;
    @Nullable
    private String description;
    @Nullable
    private String requirement;

    @Override
    public CntRequestModel apply(CntRequestModel model) {
        if(removeClaimer) {
            model.setClaimer(null);
        }

        if(claimer != null) {
            model.setClaimer(claimer);
        }

        if(coinValue != null) {
            model.setCoinValue(coinValue);
        }

        if(description != null) {
            model.setDescription(description);
        }

        if(requirement != null) {
            model.setRequirement(requirement);
        }

        return model;
    }

    public String toJson() {
        return DungeonHubService.getInstance().getGson().toJson(this);
    }
}