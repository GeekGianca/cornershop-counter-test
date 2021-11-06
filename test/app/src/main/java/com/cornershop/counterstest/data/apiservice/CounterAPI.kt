package com.cornershop.counterstest.data.apiservice

import com.cornershop.counterstest.data.models.CounterModel
import com.cornershop.counterstest.data.models.DetailCounterModel
import com.cornershop.counterstest.data.models.TitleCounterModel
import retrofit2.http.*

interface CounterAPI {
    @GET("/api/v1/counters")
    suspend fun getCounters(): List<DetailCounterModel>

    @POST("/api/v1/counter")
    suspend fun createCounterName(@Body create: TitleCounterModel): List<DetailCounterModel>

    @POST("/api/v1/counter/inc")
    suspend fun incCounter(@Body inc: CounterModel): List<DetailCounterModel>

    @POST("/api/v1/counter/dec")
    suspend fun decCounter(@Body inc: CounterModel): List<DetailCounterModel>

    @HTTP(method = "DELETE", path = "/api/v1/counter", hasBody = true)
    suspend fun removeCounterName(@Body remove: CounterModel): List<DetailCounterModel>
}