package com.cornershop.counterstest.data.mappers

interface EntityMapper<Entity, DomainModel> {
    fun toModel(entity: Entity): DomainModel
    fun toEntity(model: DomainModel): Entity
    fun toModelList(eList: List<Entity>): List<DomainModel> {
        return emptyList()
    }

    fun toEntityList(mList: List<DomainModel>): List<Entity> {
        return emptyList()
    }
}