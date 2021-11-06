package com.cornershop.counterstest.data.local.dao

import androidx.room.*
import com.cornershop.counterstest.data.local.entities.CounterEntity

@Dao
interface CounterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg counter: CounterEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(counter: CounterEntity)

    @Query("SELECT * FROM counter WHERE id = :id")
    suspend fun selectCounterById(id: String): CounterEntity

    @Query("SELECT * FROM counter WHERE title LIKE '%' || :title || '%'")
    suspend fun selectCounterByTitle(title: String): List<CounterEntity>

    @Query("SELECT * FROM counter")
    suspend fun selectAllCounters(): List<CounterEntity>

    @Query("DELETE FROM counter")
    suspend fun deleteAll()

    @Query("DELETE FROM counter WHERE id=:id")
    suspend fun deleteById(id: String)
}