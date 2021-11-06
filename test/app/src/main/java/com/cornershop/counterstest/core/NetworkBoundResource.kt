package com.cornershop.counterstest.core

import android.util.Log
import com.cornershop.counterstest.core.Util.EMPTY_LIST
import com.cornershop.counterstest.core.Util.NETWORK_ERROR
import com.cornershop.counterstest.core.Util.NETWORK_ERROR_OFFLINE
import com.cornershop.counterstest.core.Util.UNKNOWN_ERROR
import com.cornershop.counterstest.domain.repositories.safeApiCall
import com.cornershop.counterstest.domain.repositories.safeCacheCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>
constructor(
    private val dispatcher: CoroutineDispatcher,
    private val stateEvent: StateEvent,
    private val apiCall: suspend () -> NetworkObj?,
    private val cacheCall: suspend () -> CacheObj?
) {
    companion object {
        private const val TAG = "NetworkBoundResource"
    }

    val result: Flow<DataState<ViewState>> = flow {
        val apiResult = safeApiCall(dispatcher) { apiCall.invoke() }
        Log.d(TAG, "$apiResult")
        when (apiResult) {

            is ApiResult.GenericError -> {
                emit(
                    DataState.error<ViewState>(
                        errorMessage = stateEvent.errorInfo() + "\n\n Reason: " + apiResult.errorMessage,
                        stateEvent = stateEvent,
                        apiResult.code ?: 0
                    )
                )
            }
            is ApiResult.NetworkError -> {
                emit(
                    DataState.error<ViewState>(
                        errorMessage = "${stateEvent.errorInfo()} \n\n Reason: $NETWORK_ERROR",
                        stateEvent = stateEvent
                    )
                )
            }
            is ApiResult.Success -> {
                if (apiResult.value == null) {
                    emit(
                        DataState.error<ViewState>(
                            errorMessage = "${stateEvent.errorInfo()} \n\n Reason: $UNKNOWN_ERROR",
                            stateEvent = stateEvent
                        )
                    )
                } else {
                    updateCache(apiResult.value)
                }
            }
        }

        //update local data from api
        val cacheResult = safeCacheCall(dispatcher) { cacheCall.invoke() }
        Log.d(TAG, "CacheResult: $cacheResult")
        when (cacheResult) {
            is CacheResult.GenericError -> {
                emit(
                    DataState.error<ViewState>(
                        errorMessage = "${stateEvent.errorInfo()} \n\n ${cacheResult.errorMessage}",
                        stateEvent = stateEvent
                    )
                )
            }
            is CacheResult.Success -> {
                if (cacheResult.value != null) {
                    if (shouldReturnCache(cacheResult.value)) {
                        // cache contains valid data
                        Log.d("AppDebug", "NetworkBoundResource: return cache with valid data")
                        // set state event to null, we want to continue showing the progress bar until the cache is updated and showed to the user
                        // this will only set the current cache, still valid (not expired) but not updated, to be shown to the UI
                        Log.d(TAG, "CACHE: ${cacheResult.value} ")
                        emit(returnSuccessCache(cacheResult.value, null))
                    } else {
                        emit(
                            DataState.error<ViewState>(
                                errorMessage = EMPTY_LIST,
                                stateEvent = stateEvent
                            )
                        )
                    }
                } else {
                    emit(
                        DataState.error<ViewState>(
                            errorMessage = EMPTY_LIST,
                            stateEvent = stateEvent
                        )
                    )
                }
            }
        }
    }

    abstract suspend fun shouldReturnCache(cacheResult: CacheObj?): Boolean

    abstract suspend fun updateCache(networkObj: NetworkObj)

    abstract fun returnSuccessCache(
        resultObj: CacheObj,
        stateEvent: StateEvent?
    ): DataState<ViewState>
}