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
import java.text.SimpleDateFormat
import java.util.Locale

class EditAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAlarmBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var alarmManager: AlarmManager
    private lateinit var targetCal: Calendar
    private var isRepeated = false
    private var isEditAction = false
    private var requestCodeCount = 0

    private var daysTrigger: List<Day> = listOf(
        Day(Calendar.MONDAY, null, false),
        Day(Calendar.TUESDAY, null, false),
        Day(Calendar.WEDNESDAY, null, false),
        Day(Calendar.THURSDAY, null, false),
        Day(Calendar.FRIDAY, null, false),
        Day(Calendar.SATURDAY, null, false),
        Day(Calendar.SUNDAY, null, false)
    )

    private var resultIntent : Intent? = null

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

            daysTrigger.forEach {day ->
                if(day.isEnabled){
                    binding.textViewDaysOfWeek.append(Day.dayOfWeekToString(day.dayOfWeek) + " ")
                }
            }
        }

        dialog.show()
    }

    private fun checkIsRepeatedAlarm() : Boolean {
        return daysTrigger.count { it.isEnabled } != 0
    }

    private fun setDaysTrigger(dayTrigger: Day, view: View) {
        (view as CheckedTextView).isChecked = dayTrigger.isEnabled
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm() {
        val alarmIntent = Intent(baseContext, AlarmActivity::class.java)
        alarmIntent.putExtra(resources.getString(R.string.intent_key_repeating_alarm), isRepeated)

        if (isRepeated) {
            setRepeatedAlarm(alarmIntent)
        } else {

            setOneTimeAlarm(alarmIntent)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setOneTimeAlarm(alarmIntent : Intent) {
        targetCal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        val alarmInterim = AlarmInterim(
            id = 0,
            title = binding.editTextTitleAlarm.text.toString(),
            hour = binding.timePicker.hour,
            minute = binding.timePicker.minute,
            responseTimeMillis = targetCal.timeInMillis,
            intentUri = alarmIntent.toUri(URI_INTENT_SCHEME),
            oneTimeRequestCode = requestCodeCount,
            daysTrigger = daysTrigger,
            isEnabled = true,
            isRepeated = isRepeated
        )

        resultIntent = Intent()
        resultIntent?.putExtra(
            resources.getString(R.string.intent_key_create_alarm),
            alarmInterim
        )


        checkRescheduleForNDays(amountDays = 1)
        setAlarm(requestCodeCount, alarmIntent)
        Log.d("AAA", "EditAct_setOneTimeAlam: $requestCodeCount ")
        requestCodeCount++
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setRepeatedAlarm(alarmIntent: Intent) {

        Log.d("AAA", "*** setRepeatedAlarm ***")
        targetCal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        for(i in 0 until 7){

            when(targetCal.get(Calendar.DAY_OF_WEEK)){
                Calendar.MONDAY ->{
                    setAlarmForDay(dayTriggerIndex = 0, alarmIntent)
                    requestCodeCount++
                }
                Calendar.TUESDAY ->{
                    setAlarmForDay(dayTriggerIndex = 1, alarmIntent)
                    requestCodeCount++
                }
                Calendar.WEDNESDAY -> {
                    setAlarmForDay(dayTriggerIndex = 2, alarmIntent)
                    requestCodeCount++
                }
                Calendar.THURSDAY ->{
                    setAlarmForDay(dayTriggerIndex = 3, alarmIntent)
                    requestCodeCount++
                }
                Calendar.FRIDAY ->{
                    setAlarmForDay(dayTriggerIndex = 4, alarmIntent)
                    requestCodeCount++
                }
                Calendar.SATURDAY -> {
                    setAlarmForDay(dayTriggerIndex = 5, alarmIntent)
                    requestCodeCount++
                }
                Calendar.SUNDAY ->
                {
                    setAlarmForDay(dayTriggerIndex = 6, alarmIntent)
                    requestCodeCount++
                }
            }
            targetCal.add(Calendar.DAY_OF_MONTH, 1)
        }

        val alarmInterim = AlarmInterim(
            id = 0,
            title = binding.editTextTitleAlarm.text.toString(),
            hour = binding.timePicker.hour,
            minute = binding.timePicker.minute,
            responseTimeMillis = targetCal.timeInMillis,
            intentUri = alarmIntent.toUri(URI_INTENT_SCHEME),
            oneTimeRequestCode = requestCodeCount,
            daysTrigger = daysTrigger,
            isEnabled = true,
            isRepeated = isRepeated
        )

        resultIntent = Intent()
        resultIntent?.putExtra(
            resources.getString(R.string.intent_key_create_alarm),
            alarmInterim
        )
    }
    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(requestCode : Int, alarmIntent: Intent){
        val operation = PendingIntent.getActivity(
            applicationContext,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Log.d("AAA", "EditAct_setRepeatedAlam: $requestCode ")
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, operation)
    }

    private fun setAlarmForDay(dayTriggerIndex : Int, alarmIntent: Intent){
        if (!daysTrigger[dayTriggerIndex].isEnabled){
            return
        }
        checkRescheduleForNDays(amountDays = 7)
        daysTrigger[dayTriggerIndex].requestCode = requestCodeCount
        setAlarm(daysTrigger[dayTriggerIndex].requestCode!!, alarmIntent)
    }

    private fun checkRescheduleForNDays(amountDays : Int)
    {
        val diff = Calendar.getInstance().timeInMillis - targetCal.timeInMillis
        if (diff > 0) targetCal.add(Calendar.DAY_OF_WEEK, amountDays)
    }

    private fun checkChanges(view: View, isChecked: Boolean) {
        (view as CheckedTextView).isChecked = !isChecked
    }

    private fun checkEnabledDaysTrigger(day : Day) {
        day.isEnabled = !day.isEnabled
    }
}