package com.cornershop.counterstest.data.repo

import android.util.Log
import com.cornershop.counterstest.core.*
import com.cornershop.counterstest.core.Util.REFRESH_CACHE_TIME
import com.cornershop.counterstest.data.apiservice.CounterAPI
import com.cornershop.counterstest.data.local.dao.CounterDao
import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.cornershop.counterstest.data.mappers.CounterMapper
import com.cornershop.counterstest.data.mappers.DetailCounterMapper
import com.cornershop.counterstest.data.models.DetailCounterModel
import com.cornershop.counterstest.data.models.TitleCounterModel
import com.cornershop.counterstest.domain.repositories.CounterRepository
import com.cornershop.counterstest.presentation.states.MainViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CounterRepositoryImpl @Inject constructor(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    val client: CounterAPI,
    val counterDao: CounterDao,
    val mapper: DetailCounterMapper,
    val countMapper: CounterMapper
) : CounterRepository {
    override fun getCounters(
        isRefresh: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> {
        return object :
            NetworkBoundResource<List<DetailCounterModel>, List<CounterEntity>, MainViewState>(
                dispatcher = dispatcher,
                stateEvent = stateEvent,
                apiCall = { client.getCounters() },
                cacheCall = { counterDao.selectAllCounters() }
            ) {
            override suspend fun updateCache(networkObj: List<DetailCounterModel>) {
                withContext(dispatcher) {
                    val currentTimestamp = System.currentTimeMillis() / 1000
                    try {
                        val counters = mapper.toEntityList(networkObj)
                        for (count in counters) {
                            count.timestamp = currentTimestamp
                            counterDao.insert(count)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun returnSuccessCache(
                resultObj: List<CounterEntity>,
                stateEvent: StateEvent?
            ): DataState<MainViewState> {
                Log.d("CounterRepo", "$resultObj")
                val viewState =
                    MainViewState(
                        listCounterView = MainViewState.ListCounterViews(
                            listCounters = resultObj
                        )
                    )
                return DataState.data<MainViewState>(data = viewState, stateEvent = stateEvent)
            }

            override suspend fun shouldReturnCache(cacheResult: List<CounterEntity>?): Boolean {
                if (cacheResult == null || cacheResult.isEmpty()) {
                    return false
                } else {
                    if (isRefresh) {
                        withContext(dispatcher) {
                            counterDao.deleteAll()
                        }
                        return false
                    } else {
                        val currentTimestamp = System.currentTimeMillis() / 1000
                        val lastRefresh = cacheResult[0].timestamp
                        lastRefresh?.let {
                            return if ((currentTimestamp - it) > REFRESH_CACHE_TIME) {
                                withContext(dispatcher) {
                                    counterDao.deleteAll()
                                }
                                false
                            } else {
                                true
                            }
                        } ?: return false
                    }
                }
            }
        }.result
    }

    override fun editCounter(
        counter: CounterEntity,
        type: Int,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> {
        return object :
            NetworkBoundResource<List<DetailCounterModel>, List<CounterEntity>, MainViewState>(
                dispatcher = dispatcher,
                stateEvent = stateEvent,
                apiCall = {
                    if (type == 0) client.incCounter(countMapper.toModel(counter))
                    else client.decCounter(countMapper.toModel(counter))
                },
                cacheCall = { counterDao.selectAllCounters() }
            ) {
            override suspend fun shouldReturnCache(cacheResult: List<CounterEntity>?): Boolean {
                if (cacheResult == null || cacheResult.isEmpty()) {
                    return false
                } else {
                    val currentTimestamp = System.currentTimeMillis() / 1000
                    val lastRefresh = cacheResult[0].timestamp
                    lastRefresh?.let {
                        return if ((currentTimestamp - it) > REFRESH_CACHE_TIME) {
                            withContext(dispatcher) {
                                counterDao.deleteAll()
                            }
                            false
                        } else {
                            true
                        }
                    } ?: return false
                }
            }

            override suspend fun updateCache(networkObj: List<DetailCounterModel>) {
                withContext(dispatcher) {
                    val currentTimestamp = System.currentTimeMillis() / 1000
                    try {
                        val counters = mapper.toEntityList(networkObj)
                        for (count in counters) {
                            count.timestamp = currentTimestamp
                            counterDao.insert(count)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun returnSuccessCache(
                resultObj: List<CounterEntity>,
                stateEvent: StateEvent?
            ): DataState<MainViewState> {
                val viewState =
                    MainViewState(editCounterView = MainViewState.EditCounterViews(counter = resultObj))
                return DataState.data(data = viewState, stateEvent = stateEvent)
            }
        }.result
    }

    override fun createCounter(
        isRefresh: Boolean,
        counter: TitleCounterModel,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> {
        return object :
            NetworkBoundResource<List<DetailCounterModel>, List<CounterEntity>, MainViewState>(
                dispatcher = dispatcher,
                stateEvent = stateEvent,
                apiCall = { client.createCounterName(counter) },
                cacheCall = { counterDao.selectAllCounters() }
            ) {
            override suspend fun shouldReturnCache(cacheResult: List<CounterEntity>?): Boolean {
                if (cacheResult == null || cacheResult.isEmpty()) {
                    return false
                } else {
                    if (isRefresh) {
                        withContext(dispatcher) {
                            counterDao.deleteAll()
                        }
                        return false
                    } else {
                        val currentTimestamp = System.currentTimeMillis() / 1000
                        val lastRefresh = cacheResult[0].timestamp
                        lastRefresh?.let {
                            return if ((currentTimestamp - it) > REFRESH_CACHE_TIME) {
                                withContext(dispatcher) {
                                    counterDao.deleteAll()
                                }
                                false
                            } else {
                                true
                            }
                        } ?: return false
                    }
                }
            }

            override suspend fun updateCache(networkObj: List<DetailCounterModel>) {
                withContext(dispatcher) {
                    val currentTimestamp = System.currentTimeMillis() / 1000
                    try {
                        val counters = mapper.toEntityList(networkObj)
                        for (count in counters) {
                            count.timestamp = currentTimestamp
                            counterDao.insert(count)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun returnSuccessCache(
                resultObj: List<CounterEntity>,
                stateEvent: StateEvent?
            ): DataState<MainViewState> {
                val viewState =
                    MainViewState(createCounterView = MainViewState.CreateCounterViews(resultObj))
                return DataState.data(data = viewState, stateEvent = stateEvent)
            }
        }.result
    }

    override fun deleteCounter(
        counter: CounterEntity,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> {
        return object :
            NetworkBoundResource<List<DetailCounterModel>, CounterEntity, MainViewState>(
                dispatcher = dispatcher,
                stateEvent = stateEvent,
                apiCall = { client.removeCounterName(countMapper.toModel(counter)) },
                cacheCall = { counterDao.selectCounterById(counter.id) }
            ) {
            override suspend fun shouldReturnCache(cacheResult: CounterEntity?): Boolean {
                cacheResult?.let { r ->
                    withContext(dispatcher) {
                        counterDao.deleteById(r.id)
                    }
                    return true
                } ?: return false
            }

            override suspend fun updateCache(networkObj: List<DetailCounterModel>) {
                withContext(dispatcher) {
                    val currentTimestamp = System.currentTimeMillis() / 1000
                    try {
                        val counters = mapper.toEntityList(networkObj)
                        for (count in counters) {
                            count.timestamp = currentTimestamp
                            counterDao.insert(count)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun returnSuccessCache(
                resultObj: CounterEntity,
                stateEvent: StateEvent?
            ): DataState<MainViewState> {
                val viewState =
                    MainViewState(deleteCounterView = MainViewState.DeleteCounterViews(counter = resultObj))
                return DataState.data(data = viewState, stateEvent = stateEvent)
            }

        }.result
    }

    override fun getCounterByQuery(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>> =
        flow {
            try {
                val findCountersByQuery = counterDao.selectCounterByTitle(query)
                val viewState = MainViewState(
                    searchCounterView = MainViewState.SearchCounterViews(
                        listCounters = findCountersByQuery
                    )
                )
                Log.d("CounterRepositoryImpl", "$viewState")
                emit(DataState.data(data = viewState, stateEvent = stateEvent))
            } catch (e: Exception) {
                Log.e("CounterRepositoryImpl", "$e")
                e.printStackTrace()
                emit(DataState.error<MainViewState>(e.message, stateEvent = stateEvent))
            }
        }
}