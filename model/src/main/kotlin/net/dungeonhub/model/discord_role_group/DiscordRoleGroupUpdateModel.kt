package net.dungeonhub.model.discord_role_group;

import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;

@NoArgsConstructor
public class DiscordRoleGroupUpdateModel implements UpdateModel<DiscordRoleGroupModel> {
    //this won't be updated, still needs to exist due to internal references
    @Override
    public DiscordRoleGroupModel apply(DiscordRoleGroupModel model) {
        return model;
    }
}