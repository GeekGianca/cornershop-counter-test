package com.cornershop.counterstest.data.di

import com.cornershop.counterstest.data.apiservice.CounterAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideDispatcherIo(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson =
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient.Builder =
        OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)

    @Singleton
    @Provides
    fun provideLoggingHeaders(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun provideRetrofit(
        gson: Gson,
        okHttp: OkHttpClient.Builder,
        interceptor: HttpLoggingInterceptor
    ): Retrofit.Builder =
        Retrofit
            .Builder()
            .baseUrl("http://cornershop.gksoftwaresolutions.com/")
            .client(okHttp.addInterceptor(interceptor).build())
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideCounterClient(retrofit: Retrofit.Builder): CounterAPI =
        retrofit
            .build()
            .create(CounterAPI::class.java)
}