package dev.almasum.health_connect.utils

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dev.almasum.health_connect.network.WebService
import dev.almasum.health_connect.network.pojo.ResponseEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale

object DataUploader {
     suspend fun uploadSteps(context: Context) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
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

            val beginDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startTime)
            val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timeNow)

            val call = WebService.getClient().insertClientStepsRecord(
                steps = count.toInt(),
                beginDate = beginDate,
                endDate = endDate,
                phoneNumber = Prefs.phone,
                zoneId = "America/New York"
            )

            call.enqueue(object : Callback<ResponseEntity> {
                override fun onResponse(
                    call: Call<ResponseEntity>,
                    response: Response<ResponseEntity>
                ) {
                    //ignored
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    //ignored
                }
            })

        } catch (e: Exception) {
            println(e.message)
        }
    }

     suspend fun uploadOxygen(context: Context) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow),
                    pageSize = 1
                )
            )
            var level = 0.0
            response.records.map {
                level = it.percentage.value
            }

            val currentTime = SimpleDateFormat("yyyy-MM-dd mm:ss", Locale.getDefault()).format(timeNow)

            val call = WebService.getClient().insertClientOxygenSaturationRecord(
                oxygenSaturationPercent = level,
                timeOxygenRecordTaken = currentTime,
                phoneNumber = Prefs.phone,
                zoneId = "America/New York"
            )

            call.enqueue(object : Callback<ResponseEntity> {
                override fun onResponse(
                    call: Call<ResponseEntity>,
                    response: Response<ResponseEntity>
                ) {
                    //ignored
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    //ignored
                }
            })

        } catch (e: Exception) {
            println(e.message)
        }
    }
}