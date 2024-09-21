package dev.almasum.health_connect.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.almasum.health_connect.utils.AlarmHelper
import dev.almasum.health_connect.utils.DataUploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AlarmHelper.setSingleAlarm(context)

        CoroutineScope(Dispatchers.IO).launch {
            DataUploader.uploadSteps(context)
            DataUploader.uploadOxygen(context)
        }
    }


}
