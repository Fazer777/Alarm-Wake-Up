package com.project.alarmwakeup.domain.alarm_clock.models

import java.io.Serializable

data class AlarmInterim(
    var id : Long,
    val title: String,
    val hour : Int,
    val minute : Int,
    val responseTimeMillis: Long,
    val intentUri: String,
    val oneTimeRequestCode : Int?,
    val daysTrigger : List<Day>,
    val isEnabled: Boolean = true,
    val isRepeated : Boolean = false
) : Serializable