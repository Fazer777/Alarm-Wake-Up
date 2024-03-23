package com.project.alarmwakeup.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
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

    private val alarmClockListMutableLive = MutableLiveData<List<AlarmInterim>>()
    val alarmClockListLive: LiveData<List<AlarmInterim>> = alarmClockListMutableLive

    private val alarmIdMutableLive = MutableLiveData<Long>()
    val alarmId: LiveData<Long> = alarmIdMutableLive

    init {
        getAlarmClocks()
    }

    fun onAddAlarmButtonClicked(alarmInterim: AlarmInterim) {
        viewModelScope.launch {
            alarmIdMutableLive.postValue(addAlarmUseCase.execute(alarmInterim = alarmInterim))
        }
    }

    fun getAlarmClocks() {
        viewModelScope.launch {
            alarmClockListMutableLive.postValue(getAlarmClocksUseCase.execute())
        }
    }

    fun onDeleteAlarmButtonCLicked(alarmClockId: Int) {
        viewModelScope.launch {
            deleteAlarmUseCase.execute(alarmClockId = alarmClockId)
        }
    }

    fun onSwitchEnablingEvent(alarmClockId: Int, isEnabled : Boolean){
        viewModelScope.launch{
            switchEnablingUseCase.execute(alarmClockId = alarmClockId, isEnabled = isEnabled)
        }
    }

}