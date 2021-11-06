package com.cornershop.counterstest.data.mappers

import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.data.models.CounterModel
import javax.inject.Inject

class CounterMapper @Inject constructor() : EntityMapper<CounterEntity, CounterModel> {
    override fun toModel(entity: CounterEntity): CounterModel =
        CounterModel(entity.id)

    override fun toEntity(model: CounterModel): CounterEntity =
        CounterEntity(model.id, "", 0, null)
}