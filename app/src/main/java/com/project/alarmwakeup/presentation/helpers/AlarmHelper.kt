package com.project.alarmwakeup.presentation.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import com.project.alarmwakeup.R
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
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

    ) {

    private val sharedPreferences: SharedPreferences
    private var lastRequestCode: Int = -1

    init {
        sharedPreferences = appContext.getSharedPreferences(
            appContext.resources.getString(R.string.shared_pref_name),
            Context.MODE_PRIVATE
        )
    }


    private fun getValueFromSharedPref() {
        lastRequestCode =
            sharedPreferences.getInt(appContext.resources.getString(R.string.request_code_name), 0)
    }

    private fun saveValueToSharedPreferences() {
        sharedPreferences.edit()
            .putInt(appContext.resources.getString(R.string.request_code_name), lastRequestCode)
            .apply()
    }

    fun scheduleOneTimeAlarm(alarmData: OneTimeAlarmData): AlarmInterim {
        getValueFromSharedPref()
        alarmData.setRequestCode(lastRequestCode)
        setOneTimeAlarm(alarmData = alarmData)
        saveValueToSharedPreferences()
        return createOneTimeAlarmObject(alarmData)
    }


    fun scheduleRepeatedAlarm(alarmData: RepeatedAlarmData) : AlarmInterim {
        getValueFromSharedPref()
        setRepeatedAlarm(alarmData = alarmData)
        saveValueToSharedPreferences()
        return createRepeatedAlarmObject(alarmData)
    }

    // TODO ("Need to do canceling alarm")
    fun cancelAlarm() {

    }
    private fun createRepeatedAlarmObject(alarmData: RepeatedAlarmData): AlarmInterim {

        return AlarmInterim(
            id = 0,
            title = alarmData.title,
            hour = alarmData.hour,
            minute = alarmData.minute,
            responseTimeMillis = 0,
            intentUri = "empty",
            oneTimeRequestCode = null ,
            daysTrigger = alarmData.daysTrigger,
            isEnabled = true,
            isRepeated = true
        )
    }

    private fun createOneTimeAlarmObject(alarmData: OneTimeAlarmData) : AlarmInterim{
        return AlarmInterim(
            id = 0,
            title = alarmData.title,
            hour = alarmData.hour,
            minute = alarmData.minute,
            responseTimeMillis = 0,
            intentUri = "empty",
            oneTimeRequestCode = lastRequestCode - 1 ,
            daysTrigger = listOf(),
            isEnabled = true,
            isRepeated = false
        )
    }



    private fun setRepeatedAlarm(alarmData: RepeatedAlarmData) {
        for (i in 0 until 7) {
            when (targetCalendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[0], alarmData)
                }

                Calendar.TUESDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[1], alarmData)
                }

                Calendar.WEDNESDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[2], alarmData)
                }

                Calendar.THURSDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[3], alarmData)
                }

                Calendar.FRIDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[4], alarmData)
                }

                Calendar.SATURDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[5], alarmData)
                }

                Calendar.SUNDAY -> {
                    setAlarmForDay(alarmData.daysTrigger[6], alarmData)
                }
            }
            //lastRequestCode++
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun setAlarmForDay(day: Day, alarmData: BaseAlarmData) {
        if (!day.isEnabled) {
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


    fun getAlarmIntent(alarmData: BaseAlarmData): Intent {
        val alarmIntent = Intent(appContext, AlarmActivity::class.java).apply {
            putExtra(appContext.resources.getString(R.string.intent_key_alarm_data), alarmData)
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
        val operation = getOperationIntent(getAlarmIntent(alarmData = alarmData), lastRequestCode)

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
        increaseRequestCode()
    }

    private fun increaseRequestCode() {
        lastRequestCode++
    }


    private fun checkRescheduleForNDays(amountDays: Int) {
        val diff = Calendar.getInstance().timeInMillis - targetCalendar.timeInMillis
        if (diff > 0) targetCalendar.add(Calendar.DAY_OF_WEEK, amountDays)
    }
}