package net.dungeonhub.model.warning;

import me.taubsie.dungeonhub.common.entity.model.UpdateModel;
import me.taubsie.dungeonhub.common.enums.WarningType;

public class WarningUpdateModel implements UpdateModel<WarningModel> {
    private WarningType warningType;
    private String reason;
    private Boolean active;

    @Override
    public WarningModel apply(WarningModel model) {
        if(warningType != null) {
            model.setWarningType(warningType);
        }

        if(reason != null) {
            model.setReason(reason);
        }

        if(active != null) {
            model.setActive(active);
        }

        return model;
    }
}