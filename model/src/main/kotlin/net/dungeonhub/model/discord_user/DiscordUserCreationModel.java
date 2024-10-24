package net.dungeonhub.model.discord_user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.taubsie.dungeonhub.common.entity.model.CreationModel;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscordUserCreationModel implements CreationModel {
    private long id;
    private UUID minecraftId;
}