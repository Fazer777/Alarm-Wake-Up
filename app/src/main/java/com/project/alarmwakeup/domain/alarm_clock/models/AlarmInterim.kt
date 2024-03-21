package com.project.alarmwakeup.domain.alarm_clock.models

import java.io.Serializable

data class AlarmInterim(
    var id : Long,
    val title: String,
    val responseTime: String,
    val responseTimeMillis: Long,
    val intentUri: String,
    val requestCode: Int,
    val isEnabled: Boolean = true
) : Serializable