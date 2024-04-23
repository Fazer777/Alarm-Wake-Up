package com.project.alarmwakeup.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.project.alarmwakeup.R
import com.project.alarmwakeup.databinding.AlarmItemRecyclerViewBinding
import com.project.alarmwakeup.domain.alarm_clock.models.AlarmInterim
import com.project.alarmwakeup.domain.alarm_clock.models.Day

class RecyclerViewAlarmAdapter : RecyclerView.Adapter<RecyclerViewAlarmAdapter.AlarmViewHolder>() {

    private val alarmList = ArrayList<AlarmInterim>()
    private var listener : OnItemClickListener? = null

    inner class AlarmViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = AlarmItemRecyclerViewBinding.bind(itemView)

        fun bind(alarmInterim: AlarmInterim) : Unit = with(binding) {
            textViewTitleAlarm.text = alarmInterim.title
            textViewResponseTime.text = getFormatTime(hour = alarmInterim.hour, minute = alarmInterim.minute)
            switchAlarmClock.isChecked =  alarmInterim.isEnabled
            displayDaysTrigger(alarmInterim.daysTrigger, alarmInterim.isRepeated)

            switchAlarmClock.setOnCheckedChangeListener { buttonView, isChecked ->
                listener?.onItemSwitch(itemView, isChecked, adapterPosition)
            }

            itemView.setOnClickListener {
                listener?.onItemShortClick(itemView, adapterPosition)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(itemView, adapterPosition)
                return@setOnLongClickListener true
            }
        }

        private fun displayDaysTrigger(daysOfWeek : List<Day>, isRepeated : Boolean) : Unit = with(binding) {
            textViewDaysOfWeek.text = ""
            if (!isRepeated){
                textViewDaysOfWeek.text = "Одноразовый"
            }
            else{
                daysOfWeek.forEach{day ->
                    Log.d("AAA", "displayDaysTrigger: ${day.isEnabled}")
                    if(day.isEnabled){
                        textViewDaysOfWeek.append(Day.dayOfWeekToString(day.dayOfWeek) + " ")
                    }
                }
            }

        }
        private fun getFormatTime(hour : Int, minute : Int) : String{
            return "$hour:$minute"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item_recycler_view, parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount(): Int {
       return alarmList.count()
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarmList[position])
    }


    fun setAdapterList(newList : List<AlarmInterim>){
        alarmList.clear()
        alarmList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getAdapterItem(position: Int) : AlarmInterim {
        return alarmList[position]
    }

    interface OnItemClickListener{
        fun onItemSwitch(itemView : View, isChecked : Boolean, position: Int)
        fun onItemShortClick(itemView: View, position: Int)
        fun onItemLongClick(itemView: View, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}