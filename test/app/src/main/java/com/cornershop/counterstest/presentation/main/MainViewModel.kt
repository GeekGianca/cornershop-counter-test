package com.cornershop.counterstest.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cornershop.counterstest.core.ChannelDataManager
import com.cornershop.counterstest.core.DataState
import com.cornershop.counterstest.core.ErrorState
import com.cornershop.counterstest.core.StateEvent
import com.cornershop.counterstest.core.Util.INVALID_STATE_EVENT
import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.data.models.TitleCounterModel
import com.cornershop.counterstest.domain.repositories.CounterRepository
import com.cornershop.counterstest.presentation.states.MainEventState
import com.cornershop.counterstest.presentation.states.MainViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: CounterRepository) : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()
    val viewSate: LiveData<MainViewState>
        get() = _viewState

    private val channelManager: ChannelDataManager<MainViewState> =
        object : ChannelDataManager<MainViewState>(Dispatchers.IO) {
            override fun handleNewData(data: MainViewState) {
                this@MainViewModel.handleNewData(data = data)
            }

        }

    val shouldDisplayProgressBar: LiveData<Boolean> = channelManager.shouldDisplayProgressBar

    val errorState: LiveData<ErrorState?>
        get() = channelManager.errorStack.errorState

    fun setupChannel() = channelManager.setupChannel()

    fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {
            val job: Flow<DataState<MainViewState>> =
                when (stateEvent) {
                    is MainEventState.GetListCountersEvent -> {
                        Log.d(TAG, "==> ${stateEvent.isRefresh}")
                        repo.getCounters(stateEvent.isRefresh, stateEvent)
                    }
                    is MainEventState.EditCounterEvent -> {
                        repo.editCounter(stateEvent.counter, stateEvent.eventIncDec, stateEvent)
                    }
                    is MainEventState.SearchCounterEvent -> {
                        repo.getCounterByQuery(stateEvent.query, stateEvent)
                    }
                    is MainEventState.CreateCounterEvent -> {
                        Log.d(TAG, "Create: ${stateEvent.counter}")
                        repo.createCounter(stateEvent.isRefresh, stateEvent.counter, stateEvent)
                    }
                    is MainEventState.DeleteCounterEvent -> {
                        clearErrorStack()
                        repo.deleteCounter(stateEvent.counter, stateEvent)
                    }
                    else -> {
                        emitInvalidStateEvent(stateEvent)
                    }
                }
            launchJob(stateEvent, job)
        }
    }

    fun clearErrorState(index: Int = 0) {
        channelManager.clearErrorState(index)
        onCleared()
    }

    fun handleNewData(data: MainViewState) {
        Log.d("MainViewModel", "$data")
        data.let { viewState ->
            viewState.listCounterView.listCounters?.let {
                setCounterList(it)
            }

            viewState.editCounterView.counter?.let {
                setCounterList(it)
            }

            viewState.searchCounterView.listCounters?.let {
                setSearchCounterList(it)
            }

            viewState.createCounterView.listCounters?.let {
                setCounterList(it)
            }

            viewState.deleteCounterView.counter?.let {
                setDeleteCounter(it)
            }
        }
    }

    private fun setDeleteCounter(counterEntity: CounterEntity) {
        val update = getCurrentViewStateOrNew()
        update.deleteCounterView.counter = counterEntity
        setViewState(update)
    }

    private fun setSearchCounterList(counterList: List<CounterEntity>) {
        val update = getCurrentViewStateOrNew()
        update.searchCounterView.listCounters = counterList
        setViewState(update)
    }

    private fun setCounterList(counterList: List<CounterEntity>) {
        val update = getCurrentViewStateOrNew()
        update.listCounterView.listCounters = counterList
        setViewState(update)
    }

    private fun setViewState(state: MainViewState) {
        _viewState.postValue(state)
    }

    private fun getCurrentViewStateOrNew(): MainViewState {
        return viewSate.value ?: initNewViewState()
    }

    private fun initNewViewState(): MainViewState {
        return MainViewState()
    }

    private fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        return channelManager.isJobAlreadyActive(stateEvent)
    }

    private fun launchJob(stateEvent: StateEvent, jobFunction: Flow<DataState<MainViewState>?>) =
        channelManager.launchJob(stateEvent, jobFunction)

    private fun emitInvalidStateEvent(stateEvent: StateEvent) = flow {
        emit(
            DataState.error<MainViewState>(
                errorMessage = INVALID_STATE_EVENT,
                stateEvent = stateEvent
            )
        )
    }

    fun createCounter(titleCounterModel: TitleCounterModel) {
        clearErrorStack()
        setStateEvent(MainEventState.CreateCounterEvent(titleCounterModel))
    }

    fun searchCounterListByQuery(query: String) {
        clearSearchState()
        setStateEvent(MainEventState.SearchCounterEvent(query))
    }

    fun clearErrorStack() {
        channelManager.clearAllErrorState()
    }

    private fun clearSearchState() {
        val update = getCurrentViewStateOrNew()
        update.searchCounterView.listCounters = null
        setViewState(update)
    }

    private fun clearEditState() {
        val update = getCurrentViewStateOrNew()
        update.editCounterView.counter = null
        setViewState(update)
    }

    private fun clearCreateState() {
        val update = getCurrentViewStateOrNew()
        update.createCounterView.listCounters = null
        setViewState(update)
    }

    private fun clearDeleteState() {
        val update = getCurrentViewStateOrNew()
        update.deleteCounterView.counter = null
        setViewState(update)
    }

    fun clearViews() {
        clearSearchState()
        clearEditState()
        clearCreateState()
        clearDeleteState()
    }
}