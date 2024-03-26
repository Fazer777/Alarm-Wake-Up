package com.project.alarmwakeup.domain.alarm_clock.models

import android.icu.util.Calendar
import java.io.Serializable

data class Day(
    val dayOfWeek: Int,
    var requestCode: Int?,
    var isEnabled : Boolean
) : Serializable {
    companion object{
        fun dayOfWeekToString(dayOfWeek: Int): String {
            return when (dayOfWeek) {
                Calendar.MONDAY -> "Пн"
                Calendar.TUESDAY -> "Вт"
                Calendar.WEDNESDAY -> "Ср"
                Calendar.THURSDAY -> "Чт"
                Calendar.FRIDAY -> "Пт"
                Calendar.SATURDAY -> "Сб"
                Calendar.SUNDAY -> "Вс"
                else -> "Error"
            }
        }
    }
}