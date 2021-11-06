package com.cornershop.counterstest.core

import com.cornershop.counterstest.core.Util.UNKNOWN_ERROR

data class DataState<T>(
    var error: ErrorState? = null,
    var data: T? = null,
    var stateEvent: StateEvent? = null
) {

    companion object {

        fun <T> error(errorMessage: String?, stateEvent: StateEvent?, code: Int = 0): DataState<T> {
            return DataState(
                error = ErrorState(errorMessage ?: UNKNOWN_ERROR, code),
                data = null,
                stateEvent = stateEvent
            )
        }

        fun <T> data(data: T? = null, stateEvent: StateEvent?): DataState<T> {
            return DataState(
                error = null,
                data = data,
                stateEvent = stateEvent
            )
        }

        fun <T> empty(stateEvent: StateEvent?): DataState<T> {
            return DataState(
                error = null,
                data = null,
                stateEvent = stateEvent
            )
        }
    }
}