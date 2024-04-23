package com.project.alarmwakeup.data.alarm_clock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.alarmwakeup.data.alarm_clock.models.AlarmClock
import kotlinx.coroutines.flow.Flow

@Dao
interface IAlarmClockDao {

    @Insert(entity = AlarmClock::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertAlarmClock(alarmClock: AlarmClock) : Long
    @Update(entity = AlarmClock::class)
    fun updateAlarmClock(alarmClock: AlarmClock)

    @Query("DELETE FROM AlarmClocks WHERE id = :alarmClockId")
    fun deleteAlarmClock(alarmClockId: Int)

    @Query("UPDATE AlarmClocks SET IsEnabled = :isEnabled WHERE id = :alarmClockId")
    fun updateEnablingAlarmClock(alarmClockId: Int, isEnabled : Boolean)

    @Query("SELECT * FROM AlarmClocks")
    fun getAllAlarmClocks() : Flow<List<AlarmClock>>
}