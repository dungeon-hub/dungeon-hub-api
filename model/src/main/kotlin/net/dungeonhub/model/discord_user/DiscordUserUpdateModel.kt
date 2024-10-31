package net.dungeonhub.model.discord_user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DiscordUserUpdateModel implements UpdateModel<DiscordUserModel> {
    private UUID minecraftId;
    private boolean removeMinecraftId = false;

    public DiscordUserUpdateModel(UUID minecraftId) {
        this.minecraftId = minecraftId;
    }

    public DiscordUserUpdateModel(boolean removeMinecraftId) {
        this.removeMinecraftId = removeMinecraftId;
    }

    @Override
    public DiscordUserModel apply(DiscordUserModel model) {
        if (removeMinecraftId) {
            model.setMinecraftId(null);
        }

        if (minecraftId != null) {
            model.setMinecraftId(minecraftId);
        }

        return model;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}