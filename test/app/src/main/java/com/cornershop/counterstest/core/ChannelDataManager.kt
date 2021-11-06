package com.cornershop.counterstest.core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class ChannelDataManager<ViewState>(private val dispatcher: CoroutineDispatcher) {
    companion object {
        private const val TAG = "ChannelDataManager"
    }

    private var channelScope: CoroutineScope? = null
    private val stateEventManager: StateEventManager = StateEventManager()

    val errorStack = ErrorStack()

    val shouldDisplayProgressBar = stateEventManager.shouldDisplayProgressBar

    fun setupChannel() {
        cancelJobs()
    }

    abstract fun handleNewData(data: ViewState)

    fun launchJob(
        state: StateEvent,
        jobFunction: Flow<DataState<ViewState>?>
    ) {
        Log.d(TAG, "$state")
        if (canExecuteNewStateEvent(state)) {
            Log.d(TAG, "canExecuteNewStateEvent")
            addStateEvent(state)
            jobFunction
                .onEach { dataState ->
                    dataState?.let { dState ->
                        withContext(dispatcher) {

                            dState.data?.let { data ->
                                handleNewData(data)
                            }
                            dState.error?.let { error ->
                                handleNewErrorState(error)
                            }
                            dState.stateEvent?.let { stateEvent ->
                                removeStateEvent(stateEvent)
                            }
                        }
                    }
                }.launchIn(getChannelScope())
        } else {
            Log.e(TAG, "NoCanExecuteNewStateEvent")
        }
    }

    private fun canExecuteNewStateEvent(stateEvent: StateEvent): Boolean {
        if (isJobAlreadyActive(stateEvent))
            return false

        if (!isErrorStackEmpty())
            return false

        return true
    }

    fun isErrorStackEmpty(): Boolean = errorStack.isStackEmpty()

    private fun handleNewErrorState(errorState: ErrorState) = appendErrorState(errorState)

    private fun appendErrorState(errorState: ErrorState) = errorStack.add(errorState)

    fun clearErrorState(index: Int = 0) = errorStack.removeAt(index)

    fun clearAllErrorState() = errorStack.clear()

    fun getActiveJobs() = stateEventManager.getActiveJobNames()

    fun clearActiveStateEventCounter() = stateEventManager.clearActiveStateEventCounter()

    fun addStateEvent(stateEvent: StateEvent) = stateEventManager.addStateEvent(stateEvent)

    fun removeStateEvent(stateEvent: StateEvent) = stateEventManager.removeStateEvent(stateEvent)

    private fun isStateEventActive(stateEvent: StateEvent) =
        stateEventManager.isStateEventActive(stateEvent)

    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean = isStateEventActive(stateEvent)

    fun getChannelScope(): CoroutineScope =
        channelScope ?: setupNewChannelScope(CoroutineScope(Dispatchers.IO))

    private fun setupNewChannelScope(coroutineScope: CoroutineScope): CoroutineScope {
        channelScope = coroutineScope
        return channelScope as CoroutineScope
    }

    private fun cancelJobs() {
        if (channelScope != null) {
            if (channelScope?.isActive == true)
                channelScope?.cancel()
            channelScope = null
        }
        clearActiveStateEventCounter()
    }
}