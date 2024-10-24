package net.dungeonhub.model.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;

@AllArgsConstructor
@Getter
@Setter
public class DiscordServerModel implements Model, CreationModel, UpdateModel<DiscordServerModel> {
    private long id;

    public static DiscordServerModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, DiscordServerModel.class);
    }

    @Override
    public DiscordServerModel apply(DiscordServerModel discordServerModel) {
        discordServerModel.setId(id);
        return discordServerModel;
    }
}