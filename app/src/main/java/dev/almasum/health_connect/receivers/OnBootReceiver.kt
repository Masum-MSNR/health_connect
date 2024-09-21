package dev.almasum.health_connect.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import dev.almasum.health_connect.utils.AlarmHelper
import dev.almasum.health_connect.utils.Prefs

class OnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "andoid.intent.action.BOOT_COMPLETED"){
            Prefs.init(context)
            if(Prefs.clientId.isNotEmpty()){
                AlarmHelper.setSingleAlarm(context)
            }
        }
    }
}
