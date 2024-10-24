package net.dungeonhub.model.warning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.discord_user.DiscordUserModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WarningEvidenceModel implements Model {
    private long id;
    private WarningModel warningModel;
    private String evidence;
    private DiscordUserModel submitter;
}