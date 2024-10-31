package net.dungeonhub.structure.entity

import net.dungeonhub.expections.EntityUnknownException
import net.dungeonhub.structure.model.CreationModel
import net.dungeonhub.structure.model.InitializeModel
import net.dungeonhub.structure.model.Model
import net.dungeonhub.structure.model.UpdateModel
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

interface EntityService<E : Entity<M>, M : Model, C : CreationModel, I : InitializeModel<E, M, C>, U : UpdateModel<M>> {
    fun loadEntityById(id: Long): Optional<E>

    fun findAllEntities(): List<E>

    fun createEntity(initalizationModel: I): E

    fun delete(id: Long): Boolean

    fun saveEntity(entity: E): E

    fun toEntity(): Function<M, E>?

    fun toModel(): Function<E, M>

    fun loadById(id: Long): Optional<M> {
        return loadEntityById(id).map(toModel())
    }

    fun findAll(): Set<M> {
        return findAllEntities().stream().map(toModel()).collect(Collectors.toSet())
    }

    fun create(model: I): M {
        return toModel().apply(createEntity(model))
    }

    fun save(entity: E): M {
        return toModel().apply(saveEntity(entity))
    }

    fun update(entity: E, updateModel: U): E {
        try {
            return saveEntity(updateEntity(entity, updateModel))
        } catch (exception: NumberFormatException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        } catch (exception: UnsupportedOperationException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }

    fun update(id: Long, updateModel: U): E {
        return loadEntityById(id)
            .map { entity -> update(entity, updateModel) }
            .orElseThrow { EntityUnknownException(id) }
    }

    /**
     * This function should be implemented by the respective service to update the entity with the values given in the update model.
     * If you want to update the entity, please use [update] instead, as that function filters some common exceptions already.
     *
     * @param entity The entity to be updated
     * @param updateModel The model containing the values to be updated
     */
    fun updateEntity(entity: E, updateModel: U): E
}