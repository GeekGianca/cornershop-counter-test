package com.cornershop.counterstest.data.di

import com.cornershop.counterstest.data.apiservice.CounterAPI
import com.cornershop.counterstest.data.local.dao.CounterDao
import com.cornershop.counterstest.data.mappers.CounterMapper
import com.cornershop.counterstest.data.mappers.DetailCounterMapper
import com.cornershop.counterstest.data.repo.CounterRepositoryImpl
import com.cornershop.counterstest.domain.repositories.CounterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideCounterRepository(
        client: CounterAPI,
        counterDao: CounterDao,
        dMapper: DetailCounterMapper,
        countMapper: CounterMapper
    ): CounterRepository {
        return CounterRepositoryImpl(Dispatchers.IO, client, counterDao, dMapper, countMapper)
    }
}