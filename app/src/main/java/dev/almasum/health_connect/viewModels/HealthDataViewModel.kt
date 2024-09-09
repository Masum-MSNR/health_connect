package dev.almasum.health_connect.viewModels

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.Instant
import java.time.temporal.ChronoUnit

class HealthDataViewModel : ViewModel() {

    private lateinit var healthConnectClient: HealthConnectClient
    val steps: MutableLiveData<String> = MutableLiveData("--")
    val heartRate: MutableLiveData<String> = MutableLiveData("--")
    val respiratoryRate: MutableLiveData<String> = MutableLiveData("--")
    val systolicBp: MutableLiveData<String> = MutableLiveData("--")
    val diastolicBp: MutableLiveData<String> = MutableLiveData("--")
    val oxygenLevel: MutableLiveData<String> = MutableLiveData("--")
    val bodyTemp: MutableLiveData<String> = MutableLiveData("--")

    private val startTime: Instant = Instant.now().truncatedTo(ChronoUnit.DAYS)
    private val timeNow: Instant = Instant.now()


    fun initHealthConnectManager(context: Context) {
        healthConnectClient = HealthConnectClient.getOrCreate(context)
    }

    suspend fun readSteps() {
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow)
                )
            )
            var count = 0L
            response.records.map {
                count += it.count
            }

            steps.postValue(count.toString())
            println("total steps: $count")
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun readHeartRate() {
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow)
                )
            )
            var bpm = 0L
            response.records.map {
                bpm = it.samples[0].beatsPerMinute
            }

            heartRate.postValue(bpm.toString())
            println("heart rate: $bpm")
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun readRespiratoryRate() {
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow)
                )
            )
            var bpm = 0.0
            response.records.map {
                bpm = it.rate
            }

            respiratoryRate.postValue(bpm.toString())
            println("respiratory rate: $bpm")
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun readBloodPressure() {
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = BloodPressureRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow)
                )
            )
            var _systolicBp = 0.0
            var _diastolicBp = 0.0
            response.records.map {
                _systolicBp = it.systolic.inMillimetersOfMercury
                _diastolicBp = it.diastolic.inMillimetersOfMercury
            }

            systolicBp.postValue(_systolicBp.toString())
            diastolicBp.postValue(_diastolicBp.toString())
            println("systolic bp: $_systolicBp")
            println("diastolic bp: $_diastolicBp")
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun readOxygenLevel() {
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow)
                )
            )
            var level = 0.0
            response.records.map {
                level = it.percentage.value
            }

            println("oxygen level: $level")
            oxygenLevel.postValue(level.toString())
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun readBodyTemperature() {
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = BodyTemperatureRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow)
                )
            )
            var temp = 0.0
            response.records.map {
                temp = it.temperature.inCelsius
            }

            bodyTemp.postValue(temp.toString())
            println("body temperature: $temp")
        } catch (e: Exception) {
            println(e.message)
        }
    }
}