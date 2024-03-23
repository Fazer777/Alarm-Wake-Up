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
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.alarmwakeup.R
import com.project.alarmwakeup.databinding.ActivityMainBinding
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
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

    private val createAlarm: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val alarmIntent = result.data
        alarmIntent?.let {intent ->
            alarmInterim = getSerializable(intent, resources.getString(R.string.intent_key_create_alarm), AlarmInterim::class.java)
            mainViewModel.onAddAlarmButtonClicked(alarmInterim!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = getSharedPreferences(
            resources.getString(R.string.shared_pref_name),
            Context.MODE_PRIVATE
        )

        initButtons()
        initRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        mainViewModel.alarmId.observe(this@MainActivity) {
            alarmId = it
            alarmInterim?.id = alarmId
            alarmAdapter.addAlarm(alarmInterim!!)
            alarmInterim = null
        }

        mainViewModel.alarmClockListLive.observe(this@MainActivity) { list ->
            alarmAdapter.setAdapterList(list)
        }
    }

    private fun initRecyclerView() = with(binding) {
        recyclerViewAlarms.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerViewAlarms.adapter = alarmAdapter

        alarmAdapter.setOnItemClickListener(object : RecyclerViewAlarmAdapter.OnItemClickListener {
            override fun onItemSwitch(itemView: View, isChecked: Boolean, position: Int) {
//                val alarmClock = alarmAdapter.getAdapterItem(position)
//                if (isChecked){
//                    scheduleAlarm(alarmClock)
//                    mainViewModel.onSwitchEnablingEvent(
//                        alarmClockId = alarmClock.id.toInt(),
//                        isEnabled = isChecked
//                    )
//                    Toast.makeText(this@MainActivity, "AlarmClock Scheduled", Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    cancelAlarm(alarmClock)
//                    mainViewModel.onSwitchEnablingEvent(
//                        alarmClockId = alarmClock.id.toInt(),
//                        isEnabled = isChecked
//                    )
//                    Toast.makeText(this@MainActivity, "AlarmClock Canceled", Toast.LENGTH_SHORT).show()
//                }
            }

            override fun onItemShortClick(itemView: View, position: Int) {

            }

            override fun onItemLongClick(itemView: View, position: Int) {

            }
        })
    }

    private fun scheduleAlarm(alarm: AlarmInterim) {
//        val alarmIntent : Intent = Intent.parseUri(alarm.intentUri, 0)
//
//        if (alarm.isRepeated){
//            setOneTimeAlarm(alarmIntent = alarmIntent, triggerTimeInMillis = alarm.responseTimeMillis, alarm.requestCodes[0])
//        }
//        else{
//            setRepeatedAlarm(alarmIntent = alarmIntent, daysOfTriggers = alarm.daysTrigger, triggerTimeInMillis = alarm.responseTimeMillis)
//        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setOneTimeAlarm(alarmIntent: Intent, triggerTimeInMillis: Long, requestCode: Int) {
//        val operation = PendingIntent.getActivity(
//            this@MainActivity,
//            requestCode,
//            alarmIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        val targetCal = Calendar.getInstance().apply {
//            timeInMillis = triggerTimeInMillis
//        }
//
//        val diff = Calendar.getInstance().timeInMillis - targetCal.timeInMillis
//        if (diff > 0) targetCal.add(Calendar.DAY_OF_WEEK, 1)
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, operation)
    }

    private fun setRepeatedAlarm(alarmIntent: Intent, daysOfTriggers: List<Int>, triggerTimeInMillis: Long, requestCodes : List<Int>) {
//        val targetCal = Calendar.getInstance().apply {
//            timeInMillis = triggerTimeInMillis
//        }
//
//        for(i in 0 until 7){
//            if (daysOfTriggers.contains(targetCal.get(Calendar.DAY_OF_WEEK))){
//                val diff = Calendar.getInstance().timeInMillis - targetCal.timeInMillis
//                if (diff > 0) targetCal.add(Calendar.DAY_OF_WEEK, 7)
//
//                val operation = PendingIntent.getActivity(
//                    this@MainActivity,
//                    requestCodeCount,
//                    alarmIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//                requestCodes.add(requestCodeCount++)
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, operation)
//            }
//            targetCal.add(Calendar.DAY_OF_MONTH, 1)
//        }
    }

    private fun cancelAlarm(alarm: AlarmInterim) {
//        val intent : Intent = Intent.parseUri(alarm.intentUri, 0)
//        val operation = PendingIntent.getActivity(
//            this@MainActivity,
//            alarm.requestCodes,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT)
//        alarmManager.cancel(operation)
    }

    private fun initButtons() = with(binding) {
        floatBtnAddAlarm.setOnClickListener {
            createAlarm.launch(Intent(this@MainActivity, EditAlarmActivity::class.java))
        }
    }

    private fun <T : Serializable?> getSerializable (intent : Intent, key:String, m_class: Class<T>) : T {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra(key, m_class)!!
        }
        else{
            intent.getSerializableExtra(key) as T
        }
    }
}