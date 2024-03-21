package com.project.alarmwakeup.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.project.alarmwakeup.data.alarm_clock.IAlarmClockDao
import com.project.alarmwakeup.data.alarm_clock.models.AlarmClock

@Database(
    version = 1,
    entities = [
        AlarmClock::class
    ]
)
abstract class AppDatabase  : RoomDatabase(){
    object Dependencies{
        private lateinit var appContext : Context
        fun init(context: Context){
            appContext = context
        }

        private val appDatabase : AppDatabase by lazy {
            Room.databaseBuilder(appContext, AppDatabase::class.java, "Alarms.db")
                .build()
        }
    }

    abstract fun alarmDao() : IAlarmClockDao

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }
}