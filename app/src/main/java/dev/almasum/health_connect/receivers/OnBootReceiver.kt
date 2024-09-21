package dev.almasum.health_connect.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "android.intent.action.BOOT_COMPLETED"){

        }
    }
}
