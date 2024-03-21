package com.project.alarmwakeup.domain.alarm_clock.usecase

import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository

class DeleteAlarmUseCase(private val alarmRepository: IAlarmClockRepository) {
    suspend fun execute (alarmClockId : Int){
        alarmRepository.deleteAlarmClockFromDb(alarmClockId = alarmClockId)
    }
}