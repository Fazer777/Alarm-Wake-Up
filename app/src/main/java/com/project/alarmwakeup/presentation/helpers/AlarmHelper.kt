package com.project.alarmwakeup.presentation.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import com.project.alarmwakeup.R
import com.project.alarmwakeup.domain.alarm_clock.models.Day
import com.project.alarmwakeup.presentation.activities.AlarmActivity
import com.project.alarmwakeup.presentation.helpers.models.BaseAlarmData
import com.project.alarmwakeup.presentation.helpers.models.OneTimeAlarmData
import com.project.alarmwakeup.presentation.helpers.models.RepeatedAlarmData
import com.project.alarmwakeup.presentation.helpers.models.ResultDataRepeatedAlarm


// AlarmHelper содержит код для планирования и отмены будильника
// Решил собрать весь код в одному месте, так удобнее будет
class AlarmHelper(
    private val appContext: Context,
    private val alarmManager: AlarmManager,
    private val targetCalendar: Calendar,
    private var lastRequestCode : Int
    ) {

    fun scheduleOneTimeAlarm(alarmData: OneTimeAlarmData): Int {
        alarmData.setRequestCode(lastRequestCode)
        setOneTimeAlarm(alarmData = alarmData)
        return lastRequestCode
    }

    fun scheduleRepeatedAlarm(alarmData: RepeatedAlarmData): ResultDataRepeatedAlarm {
        setRepeatedAlarm(alarmData = alarmData)
        return ResultDataRepeatedAlarm(lastRequestCode, alarmData.daysTrigger)
    }

    // TODO ("Need to do canceling alarm")
    fun cancelAlarm() {

    }

    private fun setRepeatedAlarm(alarmData: RepeatedAlarmData) {
        for (i in 0 until 7) {
            when (targetCalendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0] , alarmData)
                }

                Calendar.TUESDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0] , alarmData)
                }

                Calendar.WEDNESDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0] , alarmData)
                }

                Calendar.THURSDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0] , alarmData)
                }

                Calendar.FRIDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0] , alarmData)
                }

                Calendar.SATURDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0] , alarmData)
                }

                Calendar.SUNDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0] , alarmData)
                }
            }
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun setAlarmForDay(day: Day, alarmData: BaseAlarmData){
        if (!day.isEnabled){
            return
        }
        checkRescheduleForNDays(amountDays = 7)
        day.requestCode = lastRequestCode
        setAlarm(alarmData)
    }

    private fun setOneTimeAlarm(alarmData: BaseAlarmData) {
        checkRescheduleForNDays(1)
        setAlarm(alarmData = alarmData)
    }

//    fun getOneTimeAlarmIntent(alarmData: OneTimeAlarmData): Intent {
//        val alarmIntent = Intent(appContext, AlarmActivity::class.java).apply {
//            putExtra(
//                appContext.resources.getString(R.string.intent_key_one_time_alarm), alarmData
//            )
//        }
//
//        return alarmIntent
//    }
//
//    fun getRepeatedAlarmIntent(): Intent {
//        val alarmIntent = Intent(appContext, AlarmActivity::class.java)
////        alarmIntent.putExtra(
////            appContext.resources.getString(R.string.intent_key_repeating_alarm),
////            alarm.isRepeated
////        )
//
//        return alarmIntent
//    }

    fun getAlarmIntent(alarmData: BaseAlarmData) : Intent {
        val alarmIntent = Intent(appContext, AlarmActivity::class.java).apply {
            putExtra(appContext.resources.getString(R.string.intent_key_alarm_data), alarmData )
        }
        return alarmIntent
    }

    private fun getRequestCode() = lastRequestCode

    fun getOperationIntent(alarmIntent: Intent, requestCode: Int): PendingIntent {
        return PendingIntent.getActivity(
            appContext,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun setAlarm(alarmData: BaseAlarmData) {
        val operation = getOperationIntent(getAlarmIntent(alarmData =  alarmData), lastRequestCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    targetCalendar.timeInMillis,
                    operation
                )
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                targetCalendar.timeInMillis,
                operation
            )
        }
        lastRequestCode++
    }


    private fun checkRescheduleForNDays(amountDays: Int) {
        val diff = Calendar.getInstance().timeInMillis - targetCalendar.timeInMillis
        if (diff > 0) targetCalendar.add(Calendar.DAY_OF_WEEK, amountDays)
    }
}