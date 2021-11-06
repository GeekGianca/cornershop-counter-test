package com.cornershop.counterstest.domain.repositories

import com.cornershop.counterstest.core.DataState
import com.cornershop.counterstest.core.StateEvent
import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.data.models.TitleCounterModel
import com.cornershop.counterstest.presentation.states.MainViewState
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    fun getCounters(isRefresh: Boolean, stateEvent: StateEvent): Flow<DataState<MainViewState>>

    fun editCounter(
        counter: CounterEntity,
        type: Int,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>>

    fun createCounter(
        isRefresh: Boolean,
        counter: TitleCounterModel,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>>

    fun deleteCounter(
        counter: CounterEntity,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>>

    fun getCounterByQuery(query: String, stateEvent: StateEvent): Flow<DataState<MainViewState>>
}