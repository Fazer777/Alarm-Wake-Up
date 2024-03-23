package com.project.alarmwakeup.data.alarm_clock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.alarmwakeup.data.alarm_clock.models.AlarmClock

@Dao
interface IAlarmClockDao {

    @Insert(entity = AlarmClock::class)
    fun insertNewAlarmClock(alarmClock: AlarmClock) : Long

    @Query("SELECT * FROM AlarmClocks")
    fun getAllAlarmClocks() : List<AlarmClock>

    @Query("DELETE FROM AlarmClocks WHERE id = :alarmClockId")
    fun deleteAlarmClock(alarmClockId: Int)

    @Query("UPDATE AlarmClocks SET IsEnabled = :isEnabled WHERE id = :alarmClockId")
    fun updateEnablingAlarmClock(alarmClockId: Int, isEnabled : Boolean)
}