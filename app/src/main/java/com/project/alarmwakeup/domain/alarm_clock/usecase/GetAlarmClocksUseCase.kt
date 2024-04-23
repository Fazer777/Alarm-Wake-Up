package com.project.alarmwakeup.domain.alarm_clock.usecase

import com.project.alarmwakeup.domain.alarm_clock.IAlarmClockRepository
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import kotlinx.coroutines.flow.Flow

class GetAlarmClocksUseCase(private val alarmRepository: IAlarmClockRepository) {
    fun execute() : Flow<List<AlarmInterim>>{
        return alarmRepository.getAllAlarmClocks()
    }
}