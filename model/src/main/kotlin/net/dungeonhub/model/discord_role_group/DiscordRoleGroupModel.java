package net.dungeonhub.model.discord_role_group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.discord_role.DiscordRoleModel;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiscordRoleGroupModel implements Model {
    private long id;
    private DiscordRoleModel discordRole;
    private DiscordRoleModel roleGroup;
}