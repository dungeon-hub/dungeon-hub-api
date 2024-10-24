package net.dungeonhub.entity

interface InitializingFactory<E : Entity> {
    fun transform(entity: E): E
}