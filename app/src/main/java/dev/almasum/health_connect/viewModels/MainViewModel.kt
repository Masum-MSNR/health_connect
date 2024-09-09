package dev.almasum.health_connect.viewModels

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    private lateinit var healthConnectClient: HealthConnectClient
    var healthConnectInitiated = false
    var permissionGranted = MutableLiveData(false)
    val availability = MutableLiveData(SDK_UNAVAILABLE)

    fun initHealthConnectManager(context: Context) {
        healthConnectClient = HealthConnectClient.getOrCreate(context)
        healthConnectInitiated = true
    }


    fun cleanHealthConnectManager() {
        healthConnectInitiated = false
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
    )

    suspend fun checkPermission() {
        permissionGranted.postValue(
            healthConnectClient.permissionController.getGrantedPermissions()
                .containsAll(permissions)
        )
    }

    fun checkAvailability(context: Context) {
        availability.value = HealthConnectClient.getSdkStatus(context)
    }
}