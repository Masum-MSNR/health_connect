package dev.almasum.health_connect.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import dev.almasum.health_connect.R
import dev.almasum.health_connect.activities.LauncherActivity
import dev.almasum.health_connect.utils.DataUploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataUploaderService : LifecycleService() {


    private val TAG = "ToSendSmsService"
    private val CHANNEL_ID = "FirebaseServiceChannel"


    private val checker = MutableLiveData(0)
    private var count = 0

    override fun onCreate() {
        super.onCreate()
        checker.observe(this) {
            if (it == 2) {
                count++
            }
            if (count == 2) {
                stopSelf()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            DataUploader.uploadSteps(applicationContext) {
                checker.postValue(2)
            }
            DataUploader.uploadOxygen(applicationContext) {
                checker.postValue(2)
            }
            DataUploader.updateBodyTemperature(applicationContext) {
                checker.postValue(2)
            }
            DataUploader.updateRespiratoryRate(applicationContext) {
                checker.postValue(2)
            }
            DataUploader.updateHeartRate(applicationContext) {
                checker.postValue(2)
            }
            DataUploader.updateDistanceRecord(applicationContext) {
                checker.postValue(2)
            }
            DataUploader.updateBloodPressure(applicationContext) {
                checker.postValue(2)
            }
        }
        startForegroundService()
    }


    private fun startForegroundService() {
        createNotificationChannel()

        val notificationIntent = Intent(this, LauncherActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Health Connect")
                .setContentText("Data Sync").setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(1, notification)
        }
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID, "Data Sync", NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Stopped")
    }
}
