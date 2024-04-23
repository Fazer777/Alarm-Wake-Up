package com.project.alarmwakeup.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import com.project.alarmwakeup.domain.alarm_clock.usecase.AddAlarmUseCase
import com.project.alarmwakeup.domain.alarm_clock.usecase.DeleteAlarmUseCase
import com.project.alarmwakeup.domain.alarm_clock.usecase.GetAlarmClocksUseCase
import com.project.alarmwakeup.domain.alarm_clock.usecase.SwitchEnablingUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    private val addAlarmUseCase: AddAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val getAlarmClocksUseCase: GetAlarmClocksUseCase,
    private val switchEnablingUseCase: SwitchEnablingUseCase
) : ViewModel() {


    fun addAlarm(alarmInterim: AlarmInterim) {
        viewModelScope.launch {
            addAlarmUseCase.execute(alarmInterim = alarmInterim)
        }
    }

    fun getAlarmClocksLive() : LiveData<List<AlarmInterim>> {
          return getAlarmClocksUseCase.execute().asLiveData()
    }

    fun deleteAlarm(alarmClockId: Int) {
        viewModelScope.launch {
            deleteAlarmUseCase.execute(alarmClockId = alarmClockId)
        }
    }

    fun switchEnablingAlarm(alarmClockId: Int, isEnabled : Boolean){
        viewModelScope.launch{
            switchEnablingUseCase.execute(alarmClockId = alarmClockId, isEnabled = isEnabled)
        }
    }
}