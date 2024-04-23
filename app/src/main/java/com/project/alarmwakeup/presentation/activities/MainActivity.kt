package com.project.alarmwakeup.presentation.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.alarmwakeup.R
import com.project.alarmwakeup.databinding.ActivityMainBinding
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import com.project.alarmwakeup.domain.alarm_clock.models.Day
import com.project.alarmwakeup.presentation.adapters.RecyclerViewAlarmAdapter
import com.project.alarmwakeup.presentation.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var sharedPreferences: SharedPreferences
    private val mainViewModel: MainViewModel by viewModel()
    private val alarmAdapter = RecyclerViewAlarmAdapter()
    private var alarmId: Long = 0
    private var alarmInterim: AlarmInterim? = null
    private var requestCodesCount = 0

    private val createAlarm: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val alarmIntent = result.data
        alarmIntent?.let { intent ->
            alarmInterim = getSerializable(
                intent,
                resources.getString(R.string.intent_key_create_alarm),
                AlarmInterim::class.java
            )
            mainViewModel.addAlarm(alarmInterim!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButtons()
        initRecyclerView()

        calendar = Calendar.getInstance()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = getSharedPreferences(
            resources.getString(R.string.shared_pref_name),
            Context.MODE_PRIVATE
        )

        mainViewModel.getAlarmClocksLive().observe(this@MainActivity){newList->
            alarmAdapter.setAdapterList(newList = newList)
        }
    }

    override fun onResume() {
        super.onResume()
        requestCodesCount = sharedPreferences.getInt(
            resources.getString(R.string.request_code_name),
            0
        )
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences
            .edit()
            .putInt(
                resources.getString(R.string.request_code_name),
                requestCodesCount
            )
            .apply()
    }




    private fun initRecyclerView() = with(binding) {
        recyclerViewAlarms.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerViewAlarms.adapter = alarmAdapter

        alarmAdapter.setOnItemClickListener(object : RecyclerViewAlarmAdapter.OnItemClickListener {
            override fun onItemSwitch(itemView: View, isChecked: Boolean, position: Int) {
                val alarmInterim = alarmAdapter.getAdapterItem(position)
                if (isChecked){
                    scheduleAlarm(alarmInterim)
                    mainViewModel.switchEnablingAlarm(
                        alarmClockId = alarmInterim.id.toInt(),
                        isEnabled = isChecked
                    )
                    Toast.makeText(this@MainActivity, "AlarmClock Scheduled", Toast.LENGTH_SHORT).show()
                }
                else{
                    cancelAlarm(alarmInterim)
                    mainViewModel.switchEnablingAlarm(
                        alarmClockId = alarmInterim.id.toInt(),
                        isEnabled = isChecked
                    )
                    Toast.makeText(this@MainActivity, "AlarmClock Canceled", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onItemShortClick(itemView: View, position: Int) {

            }

            // TODO ("in the future, need do alert dialog)
            override fun onItemLongClick(itemView: View, position: Int) {
                val alarmInterim = alarmAdapter.getAdapterItem(position)
                mainViewModel.deleteAlarm(alarmInterim.id.toInt())
                cancelAlarm(alarmInterim)
                when(alarmInterim.isRepeated){
                    true ->{
                        val countDays = alarmInterim.daysTrigger.count{it.isEnabled}
                        for(i in 0 until countDays){
                            requestCodesCount--
                        }
                    }
                    false ->{
                        requestCodesCount--
                    }
                }

            }
        })
    }

    private fun scheduleAlarm(alarm: AlarmInterim) {
        //val alarmIntent: Intent = Intent.parseUri(alarm.intentUri, Intent.URI_INTENT_SCHEME)
        val alarmIntent: Intent = Intent(baseContext, AlarmActivity::class.java)
        alarmIntent.putExtra(resources.getString(R.string.intent_key_repeating_alarm), alarm.isRepeated)

        if (!alarm.isRepeated) {
            setOneTimeAlarm(
                alarmIntent = alarmIntent,
                triggerTimeInMillis = alarm.responseTimeMillis,
                alarm.oneTimeRequestCode!!
            )
        } else {
            setRepeatedAlarm(
                alarmIntent = alarmIntent,
                daysTriggers = alarm.daysTrigger,
                hour = alarm.hour,
                minute = alarm.minute
            )
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setOneTimeAlarm(alarmIntent: Intent, triggerTimeInMillis: Long, requestCode: Int) {
        calendar = Calendar.getInstance().apply {
            timeInMillis = triggerTimeInMillis
        }

        checkRescheduleForNDays(1)
        setAlarm(requestCode, alarmIntent)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setRepeatedAlarm(
        alarmIntent: Intent,
        daysTriggers: List<Day>,
        hour : Int,
        minute : Int
    ) {

        calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        for (i in 0 until 7) {

            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> {
                    setAlarmForDay(daysTriggers[0] , alarmIntent)
                }

                Calendar.TUESDAY -> {
                    setAlarmForDay(daysTriggers[1], alarmIntent)
                }

                Calendar.WEDNESDAY -> {
                    setAlarmForDay(daysTriggers[2], alarmIntent)
                }

                Calendar.THURSDAY -> {
                    setAlarmForDay(daysTriggers[3], alarmIntent)
                }

                Calendar.FRIDAY -> {
                    setAlarmForDay(daysTriggers[4], alarmIntent)
                }

                Calendar.SATURDAY -> {
                    setAlarmForDay(daysTriggers[5], alarmIntent)
                }

                Calendar.SUNDAY -> {
                    setAlarmForDay(daysTriggers[6], alarmIntent)
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(requestCode: Int, alarmIntent: Intent) {
        val operation = PendingIntent.getActivity(
            applicationContext,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, operation)
    }

    private fun setAlarmForDay(
        day : Day,
        alarmIntent: Intent
    ) {
        if (!day.isEnabled) {
            return
        }
        checkRescheduleForNDays(amountDays = 7)
        setAlarm(day.requestCode!!, alarmIntent)
    }

    private fun checkRescheduleForNDays(amountDays: Int) {
        val diff = Calendar.getInstance().timeInMillis - calendar.timeInMillis
        if (diff > 0) calendar.add(Calendar.DAY_OF_WEEK, amountDays)
    }

    private fun cancelAlarm(alarm: AlarmInterim) {
        if (!alarm.isRepeated) {
            Log.d("AAA", "cancelOneTimeAlarm: ${alarm.oneTimeRequestCode} ")
            cancel(alarm.oneTimeRequestCode!!, alarm.isRepeated)
        } else {
            alarm.daysTrigger.forEach { day ->
                if (day.isEnabled) {
                    Log.d("AAA", "cancelRepeatedAlarm: ${day.requestCode!!} ")
                    cancel(day.requestCode!!, isRepeated = true)
                }
            }
        }
    }

    // TODO ("Working Incorrect")
    private fun cancel(requestCode: Int, isRepeated: Boolean) {
        val intent = Intent(baseContext, AlarmActivity::class.java)
        intent.putExtra(resources.getString(R.string.intent_key_repeating_alarm), isRepeated)
        val operation = PendingIntent.getActivity(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(operation)
    }

    private fun initButtons() = with(binding) {
        floatBtnAddAlarm.setOnClickListener {
            createAlarm.launch(Intent(this@MainActivity, EditAlarmActivity::class.java))
        }
    }

    private fun <T : Serializable?> getSerializable(
        intent: Intent,
        key: String,
        m_class: Class<T>
    ): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, m_class)!!
        } else {
            intent.getSerializableExtra(key) as T
        }
    }
}