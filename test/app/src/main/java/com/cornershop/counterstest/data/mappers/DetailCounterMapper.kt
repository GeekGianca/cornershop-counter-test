package com.cornershop.counterstest.data.mappers

import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.data.models.DetailCounterModel
import javax.inject.Inject

class DetailCounterMapper @Inject constructor() : EntityMapper<CounterEntity, DetailCounterModel> {
    override fun toModel(entity: CounterEntity): DetailCounterModel =
        DetailCounterModel(entity.id, entity.title, entity.count)

    override fun toEntity(model: DetailCounterModel): CounterEntity =
        CounterEntity(model.id, model.title, model.count, null)

    override fun toModelList(eList: List<CounterEntity>): List<DetailCounterModel> =
        eList.map { DetailCounterModel(it.id, it.title, it.count) }

    override fun toEntityList(mList: List<DetailCounterModel>): List<CounterEntity> =
        mList.map { CounterEntity(it.id, it.title, it.count, null) }


}