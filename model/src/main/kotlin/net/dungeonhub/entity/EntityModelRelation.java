package net.dungeonhub.entity;

import me.taubsie.dungeonhub.common.entity.model.Model;
import org.jetbrains.annotations.NotNull;

public interface EntityModelRelation<M extends Model> extends Entity {
    @NotNull Entity fromModel(@NotNull M model);

    @NotNull M toModel();
}
