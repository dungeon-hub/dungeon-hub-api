package net.dungeonhub.model.discord_role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscordRoleCreationModel implements CreationModel {
    private long id;
    private String nameSchema;
    private boolean verifiedRole;

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}