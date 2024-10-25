package net.dungeonhub.model.purge_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.model.PurgeTypeRoleModel;
import me.taubsie.dungeonhub.common.model.carry_type.CarryTypeModel;

import java.util.List;

@AllArgsConstructor
@Getter
public class PurgeTypeModel implements Model {
    private long id;
    private String identifier;
    @Setter
    private String displayName;
    private CarryTypeModel carryType;
    @Setter
    private List<PurgeTypeRoleModel> purgeTypeRoleModels;
}