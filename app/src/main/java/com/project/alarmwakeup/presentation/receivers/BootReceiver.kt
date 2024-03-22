package com.project.alarmwakeup.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action){
            val string = intent.getStringExtra("Key")
            Log.d("AAA", "onReceive: $string ")
        }
    }
}