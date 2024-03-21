package com.project.alarmwakeup.presentation.di

import android.content.Context
import androidx.room.Room
import com.project.alarmwakeup.data.alarm_clock.AlarmClockRepositoryImpl
import com.project.alarmwakeup.data.alarm_clock.IAlarmClockDao
import com.project.alarmwakeup.data.database.AppDatabase
import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository
import org.koin.dsl.module



val dataModule = module {
    single<IAlarmClockRepository>{
        AlarmClockRepositoryImpl(alarmDao = get())
    }

    fun providesDao(db : AppDatabase) : IAlarmClockDao = db.alarmDao()

    fun providesDatabase(context : Context): AppDatabase =
        Room.databaseBuilder(context,AppDatabase::class.java,"Alarms.db")
            .build()

    single<IAlarmClockDao> {
        providesDao(get())
    }

    single<AppDatabase> {
        providesDatabase(context = get())
    }
}