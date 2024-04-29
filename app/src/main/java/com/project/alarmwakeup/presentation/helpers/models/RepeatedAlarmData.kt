package com.project.alarmwakeup.presentation.helpers.models

import com.project.alarmwakeup.domain.alarm_clock.models.Day
import java.io.Serializable

class RepeatedAlarmData (
    title : String,
    hour : Int,
    minute : Int,
    val daysTrigger : List<Day>
) : BaseAlarmData(title, hour, minute, true){

}