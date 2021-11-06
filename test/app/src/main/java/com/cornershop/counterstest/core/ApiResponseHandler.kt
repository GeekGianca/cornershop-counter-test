package com.cornershop.counterstest.core

import com.cornershop.counterstest.core.Util.NETWORK_ERROR
import com.cornershop.counterstest.core.Util.UNKNOWN_ERROR

abstract class ApiResponseHandler<ViewState, Data>(
    response: ApiResult<Data?>,
    stateEvent: StateEvent
) {
    val result: DataState<ViewState> = when (response) {
        is ApiResult.GenericError -> {
            DataState.error(
                errorMessage = "${stateEvent.errorInfo()} \n\n Reason: ${response.errorMessage}",
                stateEvent = stateEvent
            )
        }
        is ApiResult.NetworkError -> {
            DataState.error(
                errorMessage = "${stateEvent.errorInfo()} + \n\n Reason: $NETWORK_ERROR",
                stateEvent = stateEvent
            )
        }
        is ApiResult.Success -> {
            if (response.value == null) {
                DataState.error(errorMessage = "${stateEvent.errorInfo()} \n\n Reason: $UNKNOWN_ERROR", stateEvent = stateEvent)
            } else {
                handleSuccess(response.value)
            }
        }
    }

    abstract fun handleSuccess(resultObj: Data): DataState<ViewState>
}