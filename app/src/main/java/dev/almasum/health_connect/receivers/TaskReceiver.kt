package dev.almasum.health_connect.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.almasum.health_connect.services.DataUploaderService
import dev.almasum.health_connect.utils.AlarmHelper

class TaskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AlarmHelper.setSingleAlarm(context)
        context.startForegroundService(Intent(context, DataUploaderService::class.java))
    }
}
