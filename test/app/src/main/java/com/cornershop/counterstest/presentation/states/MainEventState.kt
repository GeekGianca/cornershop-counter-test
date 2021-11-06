package com.cornershop.counterstest.presentation.states

import com.cornershop.counterstest.core.StateEvent
import com.cornershop.counterstest.core.Util.UNABLE_TO_EDIT_COUNTER
import com.cornershop.counterstest.core.Util.UNABLE_TO_RETRIEVE_COUNTERS
import com.cornershop.counterstest.core.Util.UNABLE_TO_SEARCH_COUNTER
import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.data.models.TitleCounterModel

sealed class MainEventState : StateEvent {
    class GetListCountersEvent(val orderBy: String? = null, val isRefresh: Boolean = false) :
        MainEventState() {
        override fun errorInfo(): String = UNABLE_TO_RETRIEVE_COUNTERS

        override fun eventName(): String = GetListCountersEvent::class.java.name

        override fun shouldDisplayProgressBar(): Boolean = true

    }

    class CreateCounterEvent(val counter: TitleCounterModel, val isRefresh: Boolean = false) :
        MainEventState() {
        override fun errorInfo(): String = UNABLE_TO_RETRIEVE_COUNTERS

        override fun eventName(): String = GetListCountersEvent::class.java.name

        override fun shouldDisplayProgressBar(): Boolean = true

    }

    class DeleteCounterEvent(val counter: CounterEntity) : MainEventState() {
        override fun errorInfo(): String = UNABLE_TO_RETRIEVE_COUNTERS

        override fun eventName(): String = GetListCountersEvent::class.java.name

        override fun shouldDisplayProgressBar(): Boolean = true

    }

    class EditCounterEvent(val counter: CounterEntity, val eventIncDec: Int) : MainEventState() {
        override fun errorInfo(): String = UNABLE_TO_EDIT_COUNTER

        override fun eventName(): String = EditCounterEvent::class.java.name

        override fun shouldDisplayProgressBar(): Boolean = true

    }

    class SearchCounterEvent(val query: String) : MainEventState() {

        override fun errorInfo(): String = UNABLE_TO_SEARCH_COUNTER

        override fun eventName(): String = SearchCounterEvent::class.java.name

        override fun shouldDisplayProgressBar(): Boolean = true

    }
}