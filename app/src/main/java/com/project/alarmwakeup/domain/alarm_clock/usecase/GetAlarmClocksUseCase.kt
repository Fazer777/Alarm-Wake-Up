package com.project.alarmwakeup.domain.alarm_clock.usecase

import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim

class GetAlarmClocksUseCase(private val alarmRepository: IAlarmClockRepository) {
    suspend fun execute() : List<AlarmInterim>{
        return alarmRepository.getAllAlarmClocksFromDb()
    }
}