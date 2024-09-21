package dev.almasum.health_connect.utils

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.almasum.health_connect.network.WebService
import dev.almasum.health_connect.receivers.TaskReceiver

class AlarmHelper {
    private fun setSingleAlarm(
        context: Context,
    ): Boolean {
        val intent = Intent(context, TaskReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeInMillis = System.currentTimeMillis() + (Prefs.interval * 60 * 1000)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                    AlarmClockInfo(timeInMillis, pIntent),
                    pIntent
                )
            } else {
                return false
            }
        } else {
            alarmManager.setAlarmClock(AlarmClockInfo(timeInMillis, pIntent), pIntent)
        }
        return true
    }
}