package com.project.alarmwakeup.presentation.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.alarmwakeup.R
import com.project.alarmwakeup.databinding.ActivityEditAlarmBinding
import com.project.alarmwakeup.databinding.DaysOfWeekDialogBinding

class EditAlarmActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timePicker.setIs24HourView(true)
        initButtons()

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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DaysOfWeekDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            checkedTvMonday.setOnClickListener {
                val isChecked = checkedTvMonday.isChecked
                checkedTvMonday.isChecked = !isChecked
                Toast.makeText(this@EditAlarmActivity, "${checkedTvMonday.isChecked}", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun scheduleAlarm() {

    }
}