package com.cornershop.counterstest.data.di

import android.content.Context
import com.cornershop.counterstest.data.local.DbSchema
import com.cornershop.counterstest.data.local.dao.CounterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext ctx: Context): DbSchema =
        DbSchema.getInstance(ctx)

    @Singleton
    @Provides
    fun provideCounterDao(schema: DbSchema): CounterDao = schema.counterDao()


}