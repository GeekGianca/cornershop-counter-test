package com.cornershop.counterstest.core

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class StateEventManager {
    private val activeStateEvents: HashMap<String, StateEvent> = HashMap()
    private val _shouldDisplayProgressBar: MutableLiveData<Boolean> = MutableLiveData()

    val shouldDisplayProgressBar: LiveData<Boolean>
        get() = _shouldDisplayProgressBar

    fun getActiveJobNames(): MutableSet<String> = activeStateEvents.keys

    fun clearActiveStateEventCounter() {
        activeStateEvents.clear()
        syncActiveNumStateEvents()
    }

    fun addStateEvent(stateEvent: StateEvent?) {
        activeStateEvents.remove(stateEvent?.eventName())
        syncActiveNumStateEvents()
    }

    fun removeStateEvent(stateEvent: StateEvent?) {
        activeStateEvents.remove(stateEvent?.eventName())
        syncActiveNumStateEvents()
    }

    fun isStateEventActive(stateEvent: StateEvent): Boolean {
        for (eventName in activeStateEvents.keys) {
            if (stateEvent.eventName().equals(eventName, ignoreCase = false))
                return true
        }
        return false
    }

    private fun syncActiveNumStateEvents() {
        var shouldDisplayProgressBar = false
        for (stateEvent in activeStateEvents.values) {
            if (stateEvent.shouldDisplayProgressBar()) {
                shouldDisplayProgressBar = true
            }
        }
        _shouldDisplayProgressBar.postValue(shouldDisplayProgressBar)
    }
}