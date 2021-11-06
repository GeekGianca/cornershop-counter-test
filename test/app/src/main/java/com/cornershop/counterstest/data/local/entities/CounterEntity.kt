package com.cornershop.counterstest.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "counter")
data class CounterEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "count")
    var count: Int,
    @ColumnInfo(name = "timestamp")
    var timestamp: Long?
)