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
    @ColumnInfo(name = "Hour")
    val hour: Int,
    @ColumnInfo(name = "Minute")
    val minute: Int,
    @ColumnInfo(name = "ResponseTimeMillis")
    val responseTimeMillis: Long,
    @ColumnInfo(name = "IntentUri")
    val intentUri: String,
    @ColumnInfo(name = "OneTimeRequestCode")
    val oneTimeRequestCode: Int?,
    @ColumnInfo(name = "DaysTriggerJson")
    val daysTriggerJson : String,
    @ColumnInfo(name = "IsEnabled")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "IsRepeated")
    val isRepeated: Boolean = false
) : Serializable