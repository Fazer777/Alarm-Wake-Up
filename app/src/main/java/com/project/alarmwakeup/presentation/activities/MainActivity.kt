package com.project.alarmwakeup.presentation.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.icu.util.Calendar.WeekData
import android.os.Build
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
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var sharedPreferences: SharedPreferences
    private val mainViewModel: MainViewModel by viewModel()
    private val alarmIntent by lazy { Intent(this@MainActivity, AlarmActivity::class.java) }
    private var requestCodeCount = 0
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

            }

            override fun onItemShortClick(itemView: View, position: Int) {

            }

            override fun onItemLongClick(itemView: View, position: Int) {

            }
        })
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