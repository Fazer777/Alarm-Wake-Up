package com.project.alarmwakeup.presentation.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.icu.util.Calendar.WeekData
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.project.alarmwakeup.R
import com.project.alarmwakeup.databinding.ActivityMainBinding
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import com.project.alarmwakeup.presentation.adapters.RecyclerViewAlarmAdapter
import com.project.alarmwakeup.presentation.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var calendar : Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var sharedPreferences: SharedPreferences
    private val mainViewModel : MainViewModel by viewModel()
    private val alarmIntent by lazy { Intent(this@MainActivity, AlarmActivity::class.java) }
    private var requestCodeCount = 0
    private val alarmAdapter = RecyclerViewAlarmAdapter()
    private var alarmId : Long = 0
    private var alarmInterim :AlarmInterim? = null

    private val createAlarm : ActivityResultLauncher<Intent>
        = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {result->



    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = getSharedPreferences(resources.getString(R.string.shared_pref_name), Context.MODE_PRIVATE)

        initButtons()
        initRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        mainViewModel.alarmId.observe(this@MainActivity){
            alarmId = it
            alarmInterim?.id = alarmId
            alarmAdapter.addAlarm(alarmInterim!!)
            alarmInterim = null
        }

        mainViewModel.alarmClockListLive.observe(this@MainActivity){list ->
            alarmAdapter.setAdapterList(list)
        }
    }

    override fun onResume() {
        super.onResume()
        requestCodeCount = sharedPreferences.getInt(resources.getString(R.string.request_code_name), 0)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences
            .edit()
            .putInt(resources.getString(R.string.request_code_name), requestCodeCount)
            .apply()
    }

    private fun initRecyclerView() = with(binding) {
        recyclerViewAlarms.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerViewAlarms.adapter = alarmAdapter

        alarmAdapter.setOnItemClickListener(object : RecyclerViewAlarmAdapter.OnItemClickListener{
            override fun onItemSwitch(itemView: View, isChecked: Boolean, position: Int) {
//                if (isChecked){
//                    scheduleAlarm(alarmIntent, alarmAdapter.getAdapterItem(position).requestCode)
//                }
//                else{
//                    cancelAlarm(alarmIntent, alarmAdapter.getAdapterItem(position).requestCode)
//                }
            }

            override fun onItemShortClick(itemView: View, position: Int) {
//                val requestCode = alarmAdapter.getAdapterItem(position).id
//                Toast.makeText(this@MainActivity, "$requestCode", Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClick(itemView: View, position: Int) {
//                val alarm = alarmAdapter.getAdapterItem(position)
//                cancelAlarm(Intent.parseUri(alarm.intentUri, 0), alarm.requestCode)
//                mainViewModel.onDeleteAlarmButtonCLicked(alarm.id.toInt())
//                alarmAdapter.deleteAlarm(position)
//                requestCodeCount--
            }
        })
    }

    private fun initButtons() = with(binding){
        floatBtnAddAlarm.setOnClickListener {
            createAlarm.launch(Intent(this@MainActivity, EditAlarmActivity::class.java))
        }
    }

//    private fun initTimePicker() {
//        val localtime = LocalTime.now()
//        val picker = MaterialTimePicker.Builder()
//            .setTimeFormat(TimeFormat.CLOCK_24H)
//            .setHour(localtime.hour)
//            .setMinute(localtime.minute)
//            .build()
//
//        picker.addOnPositiveButtonClickListener {
//            calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
//            calendar.set(Calendar.MINUTE, picker.minute)
//            calendar.add(Calendar.HOUR_OF_DAY, 24)
//            createAlarmClock()
//        }
//        picker.addOnNegativeButtonClickListener {
//            picker.dismiss()
//        }
//
//        picker.show(supportFragmentManager, "timePicker")
//    }

//    private fun createAlarmClock() {
//        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
//        val responseTime = simpleDateFormat.format(calendar.time)
//        scheduleAlarm(alarmIntent, requestCodeCount)
//        alarmInterim = AlarmInterim(0,"Test", responseTime, calendar.timeInMillis, alarmIntent.toUri(0)!!,requestCodeCount, true)
//        mainViewModel.onAddAlarmButtonClicked(alarmInterim!!)
//        requestCodeCount++
//    }
//
//    private fun getAlarmAction(intent : Intent, requestCode: Int) : PendingIntent{
//        return PendingIntent.getActivity(this@MainActivity, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }
//
//    private fun cancelAlarm(intent: Intent, requestCode : Int) {
//        val pendingIntent = PendingIntent.getActivity(this@MainActivity, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        alarmManager.cancel(pendingIntent)
//    }
//
//    @SuppressLint("ScheduleExactAlarm")
//    private fun scheduleAlarm(intent: Intent, requestCode: Int){
//        alarmManager.setAlarmClock(getAlarmInfo(), getAlarmAction(intent,  requestCode))
//    }
//
//    private fun getAlarmInfo(): AlarmManager.AlarmClockInfo {
//        val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//        val pendingIntent = PendingIntent.getActivity(this@MainActivity, requestCodeCount, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        return AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
//    }
}