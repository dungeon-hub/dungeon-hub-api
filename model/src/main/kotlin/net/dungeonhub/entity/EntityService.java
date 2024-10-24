package net.dungeonhub.entity;

import me.taubsie.dungeonhub.common.entity.model.CreationModel;
import me.taubsie.dungeonhub.common.entity.model.InitializeModel;
import me.taubsie.dungeonhub.common.entity.model.Model;
import me.taubsie.dungeonhub.common.entity.model.UpdateModel;
import me.taubsie.dungeonhub.common.exceptions.EntityUnknownException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface EntityService<E extends EntityModelRelation<M>, M extends Model, C extends CreationModel, I extends InitializeModel<E, C>, U extends UpdateModel<M>> {
    Optional<E> loadEntityById(long id);

    Optional<E> loadEntityByName(String name);

    List<E> findAllEntities();

    E createEntity(I initalizationModel);

    boolean delete(long id);

    E saveEntity(E entity);

    Function<M, E> toEntity();

    Function<E, M> toModel();

    default Optional<M> loadById(long id) {
        return loadEntityById(id).map(toModel());
    }

    default Optional<M> loadByName(String name) {
        return loadEntityByName(name).map(toModel());
    }

    default Set<M> findAll() {
        return findAllEntities().stream().map(toModel()).collect(Collectors.toSet());
    }

    default M create(I model) {
        return toModel().apply(createEntity(model));
    }

    default M save(E entity) {
        return toModel().apply(saveEntity(entity));
    }

    default E update(Long id, U updateModel) {
        try {
            //noinspection unchecked
            return loadEntityById(id)
                    .map(e -> e.fromModel(updateModel.apply(e.toModel())))
                    .map(entity -> saveEntity((E) entity)).orElseThrow(() -> new EntityUnknownException(id));
        }
        catch (NumberFormatException | UnsupportedOperationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    default E update(E entity, U updateModel) {
        try {
            //noinspection unchecked
            return saveEntity((E) entity.fromModel(updateModel.apply(entity.toModel())));
        }
        catch (NumberFormatException | UnsupportedOperationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}