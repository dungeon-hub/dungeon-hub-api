package net.dungeonhub.model.discord_role;

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
public class DiscordRoleUpdateModel implements UpdateModel<DiscordRoleModel> {
    private String nameSchema;
    private Boolean verifiedRole;

    @Override
    public DiscordRoleModel apply(DiscordRoleModel model) {
        if (nameSchema != null) {
            model.setNameSchema(nameSchema);
        }

        if (verifiedRole != null) {
            model.setVerifiedRole(verifiedRole);
        }

        return model;
    }

    public String toJson() {
        return DungeonHubService.getInstance()
                .getGson()
                .toJson(this);
    }
}