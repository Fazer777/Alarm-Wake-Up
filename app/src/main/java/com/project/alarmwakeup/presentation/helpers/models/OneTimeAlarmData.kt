package com.project.alarmwakeup.presentation.helpers.models

import java.io.Serializable

class OneTimeAlarmData(
    title: String,
    hour: Int,
    minute: Int,
) : BaseAlarmData(title, hour, minute, false) {

    private val DEFAULT_CODE = -1

    private var requestCode: Int = DEFAULT_CODE

    fun setRequestCode(requestCode: Int) {
        this.requestCode = requestCode
    }


}