package net.dungeonhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.discord_role.DiscordRoleModel;
import me.taubsie.dungeonhub.common.model.purge_type.PurgeTypeModel;

@Getter
@AllArgsConstructor
public class PurgeTypeRoleModel implements Model {
    private PurgeTypeModel purgeTypeModel;
    private DiscordRoleModel discordRoleModel;
}