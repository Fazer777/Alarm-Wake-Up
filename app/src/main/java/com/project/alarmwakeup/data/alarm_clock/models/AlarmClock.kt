package com.project.alarmwakeup.data.alarm_clock.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "AlarmClocks")
data class AlarmClock(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    @ColumnInfo(name = "Title")
    val title: String,
    @ColumnInfo(name = "ResponseTime")
    val responseTime: String,
    @ColumnInfo(name = "ResponseTimeMillis")
    val responseTimeMillis: Long,
    @ColumnInfo(name = "IntentUri")
    val intentUri: String,
    @ColumnInfo(name = "RequestCode")
    val requestCode: Int,
    @ColumnInfo(name = "IsEnabled")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "DaysTriggerBlob")
    val daysTriggerBlob : String,
    @ColumnInfo(name = "IsRepeated")
    val isRepeated: Boolean = false
) : Serializable