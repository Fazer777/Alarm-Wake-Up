package com.project.alarmwakeup.domain.alarm_clock.usecase

import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim

class AddAlarmUseCase(private val alarmRepository: IAlarmClockRepository) {
    suspend fun execute(alarmInterim: AlarmInterim) : Long{
        return alarmRepository.insertAlarmClockToDb(alarmInterim = alarmInterim)
    }

}