package com.project.alarmwakeup.presentation.helpers.models

import java.io.Serializable

open class BaseAlarmData(
    val title : String,
    val hour : Int,
    val minute : Int,
    val isRepeated : Boolean
) : Serializable {

}