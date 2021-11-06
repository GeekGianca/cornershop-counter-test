package com.cornershop.counterstest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cornershop.counterstest.R
import com.cornershop.counterstest.data.local.dao.CounterDao
import com.cornershop.counterstest.data.local.entities.CounterEntity

@Database(entities = [CounterEntity::class], version = 1, exportSchema = false)
abstract class DbSchema : RoomDatabase() {
    abstract fun counterDao(): CounterDao

    companion object {
        @Volatile
        private var INSTANCE: DbSchema? = null

        fun getInstance(context: Context): DbSchema {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DbSchema::class.java,
                    context.resources.getString(R.string.database_name)
                )
                    .addCallback(sRoomDatabaseCallback)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val sRoomDatabaseCallback: Callback =
            object : Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                }
            }
    }
}