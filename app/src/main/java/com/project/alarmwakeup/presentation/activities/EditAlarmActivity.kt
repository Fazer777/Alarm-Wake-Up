package com.project.alarmwakeup.presentation.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.URI_INTENT_SCHEME
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.alarmwakeup.R
import com.project.alarmwakeup.databinding.ActivityEditAlarmBinding
import com.project.alarmwakeup.databinding.DaysOfWeekDialogBinding
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import com.project.alarmwakeup.domain.alarm_clock.models.Day
import com.project.alarmwakeup.presentation.helpers.AlarmHelper
import com.project.alarmwakeup.presentation.helpers.models.OneTimeAlarmData
import com.project.alarmwakeup.presentation.helpers.models.RepeatedAlarmData
import java.text.SimpleDateFormat
import java.util.Locale

class EditAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAlarmBinding

    private lateinit var alarmManager: AlarmManager
    private lateinit var targetCal: Calendar
    private var isRepeated = false
    private var isEditAction = false


    private var daysTrigger: List<Day> = listOf(
        Day(Calendar.MONDAY, null, false),
        Day(Calendar.TUESDAY, null, false),
        Day(Calendar.WEDNESDAY, null, false),
        Day(Calendar.THURSDAY, null, false),
        Day(Calendar.FRIDAY, null, false),
        Day(Calendar.SATURDAY, null, false),
        Day(Calendar.SUNDAY, null, false)
    )

    private var resultIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        binding.timePicker.setIs24HourView(true)
        initButtons()
        getMyIntent()

    }



    private fun getMyIntent() {
        val bundle = intent.extras
        isEditAction = bundle != null
        Toast.makeText(this@EditAlarmActivity, "$isEditAction", Toast.LENGTH_SHORT).show()
        if (isEditAction) {
            setData()
        }
    }

    private fun setData() {
        // TODO ("EDIT ACTION IS NOT IMPLEMENTED")
    }

    private fun initButtons() = with(binding) {
        materialBtnScheduleAlarm.setOnClickListener {
            scheduleAlarm()
        }

        textViewDaysOfWeek.setOnClickListener {
            initDaysOfWeekDialog()
        }
    }

    private fun initDaysOfWeekDialog() {
        val dialog = Dialog(this@EditAlarmActivity)

        val dialogBinding = DaysOfWeekDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.apply {

            setDaysTrigger(daysTrigger[0], checkedTvMonday)
            setDaysTrigger(daysTrigger[1], checkedTvTuesday)
            setDaysTrigger(daysTrigger[2], checkedTvWednesday)
            setDaysTrigger(daysTrigger[3], checkedTvThursday)
            setDaysTrigger(daysTrigger[4], checkedTvFriday)
            setDaysTrigger(daysTrigger[5], checkedTvSaturday)
            setDaysTrigger(daysTrigger[6], checkedTvSunday)


            var isChecked: Boolean
            checkedTvMonday.setOnClickListener {
                isChecked = checkedTvMonday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(daysTrigger[0])

            }

            checkedTvTuesday.setOnClickListener {
                isChecked = checkedTvTuesday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(daysTrigger[1])
            }

            checkedTvWednesday.setOnClickListener {
                isChecked = checkedTvWednesday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(daysTrigger[2])
            }

            checkedTvThursday.setOnClickListener {
                isChecked = checkedTvThursday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(daysTrigger[3])
            }

            checkedTvFriday.setOnClickListener {
                isChecked = checkedTvFriday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(daysTrigger[4])
            }

            checkedTvSaturday.setOnClickListener {
                isChecked = checkedTvSaturday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(daysTrigger[5])
            }

            checkedTvSunday.setOnClickListener {
                isChecked = checkedTvSunday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(daysTrigger[6])
            }
        }

        dialog.setOnCancelListener {
            isRepeated = checkIsRepeatedAlarm()
            binding.textViewDaysOfWeek.text = ""

            daysTrigger.forEach { day ->
                if (day.isEnabled) {
                    binding.textViewDaysOfWeek.append(Day.dayOfWeekToString(day.dayOfWeek) + " ")
                }
            }
        }

        dialog.show()
    }

    private fun checkIsRepeatedAlarm(): Boolean {
        return daysTrigger.count { it.isEnabled } != 0
    }

    private fun setDaysTrigger(dayTrigger: Day, view: View) {
        (view as CheckedTextView).isChecked = dayTrigger.isEnabled
    }


    private fun scheduleAlarm() {

        targetCal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
        }


        if (isRepeated) {
           setRepeatedAlarm()
        } else {
            setOneTimeAlarm()
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun setOneTimeAlarm() {

        val alarmHelper = AlarmHelper(applicationContext, alarmManager, targetCal)

        val oneTimeAlarmData = OneTimeAlarmData(
            title = binding.editTextTitleAlarm.text.toString(),
            hour = binding.timePicker.hour,
            minute = binding.timePicker.minute
        )

        val alarmInterim = alarmHelper.scheduleOneTimeAlarm(alarmData = oneTimeAlarmData)

        resultIntent = Intent()
        resultIntent?.putExtra(
            resources.getString(R.string.intent_key_create_alarm),
            alarmInterim
        )
    }

    private fun setRepeatedAlarm() {

        val alarmHelper = AlarmHelper(applicationContext, alarmManager, targetCal)

        val repeatedAlarmData = RepeatedAlarmData(
            title = binding.editTextTitleAlarm.text.toString(),
            hour = binding.timePicker.hour,
            minute = binding.timePicker.minute,
            daysTrigger =  daysTrigger
        )

        val alarmInterim = alarmHelper.scheduleRepeatedAlarm(alarmData = repeatedAlarmData)

        resultIntent = Intent()
        resultIntent?.putExtra(
            resources.getString(R.string.intent_key_create_alarm),
            alarmInterim
        )
    }

    private fun checkChanges(view: View, isChecked: Boolean) {
        (view as CheckedTextView).isChecked = !isChecked
    }

    private fun checkEnabledDaysTrigger(day: Day) {
        day.isEnabled = !day.isEnabled
    }
}