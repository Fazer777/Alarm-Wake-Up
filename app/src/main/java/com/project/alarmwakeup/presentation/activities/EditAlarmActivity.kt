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
import java.text.SimpleDateFormat
import java.util.Locale

class EditAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAlarmBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var targetCal: Calendar
    private var isRepeated = false
    private var isEditAction = false
    private var daysTrigger: ArrayList<Int> = arrayListOf()
    private val requestCodes: ArrayList<Int> = arrayListOf()
    private var requestCodeCount = 0
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager



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
                binding.textViewDaysOfWeek.append(Day.dayOfWeekToString(day) + " ")
            }
        }

        dialog.show()
    }

    private fun setDaysTrigger(dayTrigger: Int, view: View) {
        (view as CheckedTextView).isChecked = daysTrigger.contains(dayTrigger)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm() {
        val alarmIntent = Intent(this@EditAlarmActivity, AlarmActivity::class.java)
        alarmIntent.putExtra(resources.getString(R.string.intent_key_repeating_alarm), isRepeated)

        if (isRepeated) {
            setRepeatedAlarm(alarmIntent, daysTrigger)
        } else {

            setOneTimeAlarm(alarmIntent)
        }

        val alarmInterim = AlarmInterim(
            id = 0,
            title = binding.editTextTitleAlarm.text.toString(),
            responseTime = getFormatTime(),
            responseTimeMillis = targetCal.timeInMillis,
            intentUri = alarmIntent.toUri(0),
            requestCodes = requestCodes,
            daysTrigger = daysTrigger,
            isEnabled = true,
            isRepeated = isRepeated
        )
        val intentStorage = Intent()
        intentStorage.putExtra(resources.getString(R.string.intent_key_create_alarm), alarmInterim)
        setResult(RESULT_OK, intentStorage)
        finish()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setOneTimeAlarm(alarmIntent : Intent) {

        val operation = PendingIntent.getActivity(
            this@EditAlarmActivity,
            requestCodeCount,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        requestCodes.add(requestCodeCount++)
        targetCal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
        }
        val diff = Calendar.getInstance().timeInMillis - targetCal.timeInMillis
        if (diff > 0) targetCal.add(Calendar.DAY_OF_WEEK, 1)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, operation)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setRepeatedAlarm(alarmIntent: Intent, daysOfTriggers: List<Int>) {


        targetCal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        for(i in 0 until 7){
            if (daysOfTriggers.contains(targetCal.get(Calendar.DAY_OF_WEEK))){
                val diff = Calendar.getInstance().timeInMillis - targetCal.timeInMillis
                if (diff > 0) targetCal.add(Calendar.DAY_OF_WEEK, 7)

                val operation = PendingIntent.getActivity(
                    this@EditAlarmActivity,
                    requestCodeCount,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                requestCodes.add(requestCodeCount++)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, operation)
            }
            targetCal.add(Calendar.DAY_OF_MONTH, 1)
        }

    }

    private fun getFormatTime(): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
        return simpleDateFormat.format(targetCal.time)
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

}