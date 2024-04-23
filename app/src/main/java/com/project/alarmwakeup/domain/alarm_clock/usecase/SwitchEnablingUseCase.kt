package com.project.alarmwakeup.domain.alarm_clock.usecase

import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository

class SwitchEnablingUseCase(private val alarmRepository: IAlarmClockRepository) {
    suspend fun execute(alarmClockId : Int, isEnabled : Boolean){
        alarmRepository.updateEnablingAlarmClock(alarmClockId = alarmClockId, isEnabled = isEnabled)
    }
}