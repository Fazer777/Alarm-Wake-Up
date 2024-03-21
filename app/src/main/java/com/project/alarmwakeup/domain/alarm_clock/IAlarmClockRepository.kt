package com.project.alarmwakeup.domain.alarm_clock

import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim

interface IAlarmClockRepository {
    suspend fun insertAlarmClockToDb(alarmInterim: AlarmInterim) : Long

    suspend fun getAllAlarmClocksFromDb() : List<AlarmInterim>

    suspend fun deleteAlarmClockFromDb(alarmClockId : Int)
}