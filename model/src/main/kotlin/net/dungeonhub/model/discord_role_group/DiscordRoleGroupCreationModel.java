package net.dungeonhub.model.discord_role_group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;
import me.taubsie.dungeonhub.common.model.discord_role.DiscordRoleModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiscordRoleGroupCreationModel implements CreationModel {
    private DiscordRoleModel discordRole;
    private DiscordRoleModel roleGroup;
}