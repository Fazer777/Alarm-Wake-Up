package com.project.alarmwakeup.domain.alarm_clock

import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import kotlinx.coroutines.flow.Flow

interface IAlarmClockRepository {
    suspend fun insertAlarmClock(alarmInterim: AlarmInterim)

    fun getAllAlarmClocks(): Flow<List<AlarmInterim>>

    suspend fun deleteAlarmClock(alarmClockId : Int)
    suspend fun updateEnablingAlarmClock(alarmClockId : Int, isEnabled : Boolean)
}