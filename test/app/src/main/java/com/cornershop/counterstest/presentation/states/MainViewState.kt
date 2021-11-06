package com.cornershop.counterstest.presentation.states

import com.cornershop.counterstest.data.local.entities.CounterEntity

data class MainViewState(
    var listCounterView: ListCounterViews = ListCounterViews(),
    var searchCounterView: SearchCounterViews = SearchCounterViews(),
    var createCounterView: CreateCounterViews = CreateCounterViews(),
    var editCounterView: EditCounterViews = EditCounterViews(),
    var deleteCounterView: DeleteCounterViews = DeleteCounterViews()
) {

    data class ListCounterViews(var listCounters: List<CounterEntity>? = null)

    data class SearchCounterViews(var listCounters: List<CounterEntity>? = null)

    data class CreateCounterViews(var listCounters: List<CounterEntity>? = null)

    data class EditCounterViews(var counter: List<CounterEntity>? = null)

    data class DeleteCounterViews(var counter: CounterEntity? = null)
}