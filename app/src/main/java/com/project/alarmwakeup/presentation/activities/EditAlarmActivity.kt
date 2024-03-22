package com.project.alarmwakeup.presentation.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.alarmwakeup.R
import com.project.alarmwakeup.databinding.ActivityEditAlarmBinding
import com.project.alarmwakeup.databinding.DaysOfWeekDialogBinding
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import java.text.SimpleDateFormat
import java.util.Locale

class EditAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAlarmBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var calendar: Calendar
    private var isRepeated = false
    private var isEditAction = false
    private var daysTrigger: ArrayList<Int> = arrayListOf()
    private var requestCodeCount = 0
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        sharedPref = getSharedPreferences(
            resources.getString(R.string.shared_pref_name),
            Context.MODE_PRIVATE
        )
        binding.timePicker.setIs24HourView(true)
        initButtons()
        getMyIntent()

    }

    override fun onResume() {
        super.onResume()
        requestCodeCount = sharedPref.getInt(resources.getString(R.string.request_code_name), 0)
    }

    override fun onPause() {
        super.onPause()
        sharedPref
            .edit()
            .putInt(resources.getString(R.string.request_code_name), requestCodeCount)
            .apply()
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
    }

    private fun initButtons() = with(binding) {
        materialBtnScheduleAlarm.setOnClickListener {
            scheduleAlarm()
            requestCodeCount++
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


            setDaysTrigger(Calendar.MONDAY, checkedTvMonday)
            setDaysTrigger(Calendar.TUESDAY, checkedTvTuesday)
            setDaysTrigger(Calendar.WEDNESDAY, checkedTvWednesday)
            setDaysTrigger(Calendar.THURSDAY, checkedTvThursday)
            setDaysTrigger(Calendar.FRIDAY, checkedTvFriday)
            setDaysTrigger(Calendar.SATURDAY, checkedTvSaturday)
            setDaysTrigger(Calendar.SUNDAY, checkedTvSunday)


            var isChecked: Boolean
            checkedTvMonday.setOnClickListener {
                isChecked = checkedTvMonday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(Calendar.MONDAY)

            }

            checkedTvTuesday.setOnClickListener {
                isChecked = checkedTvTuesday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(Calendar.TUESDAY)
            }

            checkedTvWednesday.setOnClickListener {
                isChecked = checkedTvWednesday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(Calendar.WEDNESDAY)
            }

            checkedTvThursday.setOnClickListener {
                isChecked = checkedTvThursday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(Calendar.THURSDAY)
            }

            checkedTvFriday.setOnClickListener {
                isChecked = checkedTvFriday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(Calendar.FRIDAY)
            }

            checkedTvSaturday.setOnClickListener {
                isChecked = checkedTvSaturday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(Calendar.SATURDAY)
            }

            checkedTvSunday.setOnClickListener {
                isChecked = checkedTvSunday.isChecked
                checkChanges(it, isChecked)
                checkEnabledDaysTrigger(Calendar.SUNDAY)
            }
        }

        dialog.setOnCancelListener {
            isRepeated = daysTrigger.isNotEmpty()

            binding.textViewDaysOfWeek.text = ""
            for (day in daysTrigger) {
                binding.textViewDaysOfWeek.append(dayOfWeekToString(day) + " ")
            }
        }

        dialog.show()
    }

    private fun setDaysTrigger(dayTrigger: Int, view: View) {
        (view as CheckedTextView).isChecked = daysTrigger.contains(dayTrigger)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm() {
        calendar.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
        calendar.set(Calendar.MINUTE, binding.timePicker.minute)
        val triggerTimeInMillis = calendar.timeInMillis

        val alarmIntent = Intent(this@EditAlarmActivity, AlarmActivity::class.java)
        alarmIntent.putExtra(resources.getString(R.string.intent_key_repeating_alarm), isRepeated)

        val operation = PendingIntent.getActivity(
            this@EditAlarmActivity,
            requestCodeCount,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (isRepeated) {
            setRepeatedAlarm(operation, daysTrigger)
        } else {

            setOneTimeAlarm(operation, triggerTimeInMillis)
        }

        val alarmInterim = AlarmInterim(
            id = 0,
            title = binding.editTextTitleAlarm.text.toString(),
            responseTime = getFormatTime(),
            responseTimeMillis = triggerTimeInMillis,
            intentUri = alarmIntent.toUri(0),
            requestCode = requestCodeCount,
            daysTrigger = daysTrigger,
            isEnabled = true,
            isRepeated = isRepeated
        )
        val intentStorage = Intent()
        intentStorage.putExtra(resources.getString(R.string.intent_key_create_alarm), alarmInterim)
        setResult(RESULT_OK, intentStorage )
        finish()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setOneTimeAlarm(operation: PendingIntent, triggerTimeInMillis: Long) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, operation)
    }

    private fun setRepeatedAlarm(operation: PendingIntent, daysOfTriggers: List<Int>) {
        val interval = AlarmManager.INTERVAL_DAY
        for (day in daysOfTriggers) {
            calendar.set(Calendar.DAY_OF_WEEK, day)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis, interval * 7, operation)
        }
    }

    private fun getFormatTime(): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
        return simpleDateFormat.format(calendar.time)
    }

    private fun checkChanges(view: View, isChecked: Boolean) {
        (view as CheckedTextView).isChecked = !isChecked
    }

    private fun checkEnabledDaysTrigger(day: Int) {
        daysTrigger.contains(day).let { isContains ->
            if (isContains)
                daysTrigger.remove(day)
            else
                daysTrigger.add(day)
        }
    }

    private fun dayOfWeekToString(dayOfWeek: Int): String {
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