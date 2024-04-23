package com.project.alarmwakeup.data.alarm_clock

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.project.alarmwakeup.data.alarm_clock.models.AlarmClock
import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import com.project.alarmwakeup.domain.alarm_clock.models.Day
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class AlarmClockRepositoryImpl(
    private val alarmDao: IAlarmClockDao,
) : IAlarmClockRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun insertAlarmClock(alarmInterim: AlarmInterim) {
        withContext(dispatcher) {
            alarmDao.insertAlarmClock(
                mapToAlarmClock(alarmInterim)
            )
        }
    }

    override fun getAllAlarmClocks(): Flow<List<AlarmInterim>> {
        return alarmDao.getAllAlarmClocks().map { list ->
                list.map { alarmClock -> mapToAlarmInterim(alarmClock) }
            }

    }

    override suspend fun deleteAlarmClock(alarmClockId: Int) {
        withContext(dispatcher) {
            alarmDao.deleteAlarmClock(alarmClockId)
        }
    }

    override suspend fun updateEnablingAlarmClock(alarmClockId: Int, isEnabled: Boolean) {
        withContext(dispatcher) {
            alarmDao.updateEnablingAlarmClock(alarmClockId = alarmClockId, isEnabled = isEnabled)
        }
    }


    private fun mapToAlarmClock(alarmInterim: AlarmInterim): AlarmClock {
        return AlarmClock(
            id = alarmInterim.id,
            title = alarmInterim.title,
            hour = alarmInterim.hour,
            minute = alarmInterim.minute,
            responseTimeMillis = alarmInterim.responseTimeMillis,
            intentUri = alarmInterim.intentUri,
            oneTimeRequestCode = alarmInterim.oneTimeRequestCode,
            daysTriggerJson = mapDaysTriggerToJson(alarmInterim.daysTrigger),
            isEnabled = alarmInterim.isEnabled,
            isRepeated = alarmInterim.isRepeated
        )
    }

    private fun mapToAlarmInterim(alarmClock: AlarmClock): AlarmInterim {
        return AlarmInterim(
            id = alarmClock.id,
            title = alarmClock.title,
            hour = alarmClock.hour,
            minute = alarmClock.minute,
            responseTimeMillis = alarmClock.responseTimeMillis,
            intentUri = alarmClock.intentUri,
            oneTimeRequestCode = alarmClock.oneTimeRequestCode,
            daysTrigger = mapDaysTriggerJsonToArray(alarmClock.daysTriggerJson),
            isEnabled = alarmClock.isEnabled,
            isRepeated = alarmClock.isRepeated
        )
    }

    private fun mapDaysTriggerToJson(daysTrigger: List<Day>): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(daysTrigger)
    }

    private fun mapDaysTriggerJsonToArray(json: String): List<Day> {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val type: Type = object : TypeToken<List<Day>>() {}.type
        return gson.fromJson(json, type)
    }

}