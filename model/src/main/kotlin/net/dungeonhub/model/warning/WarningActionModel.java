package net.dungeonhub.model.warning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.taubsie.dungeonhub.common.enums.WarningAction;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class WarningActionModel {
    @NotNull
    private WarningAction warningAction;
    private String data;
}