package com.project.alarmwakeup.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.project.alarmwakeup.data.alarm_clock.IAlarmClockDao
import com.project.alarmwakeup.data.alarm_clock.models.AlarmClock

@Database(
    version = 1,
    entities = [
        AlarmClock::class
    ],
    exportSchema = false
)
abstract class AppDatabase  : RoomDatabase(){
    object Dependencies{

//        private val MIGRATION_1_2 = object : Migration(1,2){
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("ALTER TABLE AlarmClocks ADD COLUMN IsRepeated INTEGER NOT NULL DEFAULT(0)")
//            }
//        }
//
//        private val MIGRATION_2_3 = object : Migration(2,3){
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("ALTER TABLE AlarmClocks ADD COLUMN DaysTriggerBlob TEXT NULL")
//            }
//        }
//
//        fun getMigration_1_2() : Migration{
//            return MIGRATION_1_2
//        }
//        fun getMigration_2_3() : Migration{
//            return MIGRATION_2_3
//        }

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