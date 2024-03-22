package com.project.alarmwakeup.data.alarm_clock

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.project.alarmwakeup.data.alarm_clock.models.AlarmClock
import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class AlarmClockRepositoryImpl(private val alarmDao : IAlarmClockDao) : IAlarmClockRepository {

    override suspend fun insertAlarmClockToDb(alarmInterim: AlarmInterim) : Long {
        return withContext(Dispatchers.IO){
            val alarmClock = mapToAlarmClock(alarmInterim)
            return@withContext alarmDao.insertNewAlarmClock(alarmClock)
        }
    }

    override suspend fun getAllAlarmClocksFromDb(): List<AlarmInterim> {
        return withContext(Dispatchers.IO){
            return@withContext alarmDao.getAllAlarmClocks().map { it -> mapToAlarmInterim(it) }
        }
    }

    override suspend fun deleteAlarmClockFromDb(alarmClockId: Int) {
        withContext(Dispatchers.IO){
            alarmDao.deleteAlarmClock(alarmClockId)
        }
    }


    private fun mapToAlarmClock(alarmInterim: AlarmInterim) : AlarmClock{
        return AlarmClock(
            id = alarmInterim.id,
            title = alarmInterim.title,
            responseTime = alarmInterim.responseTime,
            responseTimeMillis = alarmInterim.responseTimeMillis,
            intentUri = alarmInterim.intentUri,
            requestCode = alarmInterim.requestCode,
            daysTriggerBlob = mapArrayToJson(alarmInterim.daysTrigger),
            isEnabled = alarmInterim.isEnabled,
            isRepeated = alarmInterim.isRepeated
        )
    }

    private fun mapToAlarmInterim(alarmClock: AlarmClock) : AlarmInterim{
        return AlarmInterim(
            id = alarmClock.id,
            title = alarmClock.title,
            responseTime = alarmClock.responseTime,
            responseTimeMillis = alarmClock.responseTimeMillis,
            intentUri = alarmClock.intentUri,
            requestCode = alarmClock.requestCode,
            daysTrigger = mapJsonToArray(alarmClock.daysTriggerBlob),
            isEnabled = alarmClock.isEnabled,
            isRepeated = alarmClock.isRepeated
        )
    }

    private fun mapArrayToJson(daysTrigger : List<Int>) : String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(daysTrigger)
    }

    private fun mapJsonToArray(json : String) : List<Int>{
        val gson = GsonBuilder().setPrettyPrinting().create()
        val type : Type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, type)
    }


}