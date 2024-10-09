package dev.almasum.health_connect.utils

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
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
    suspend fun uploadSteps(context: Context, onDone: () -> Unit) {
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

            val beginDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(System.currentTimeMillis() - (Prefs.interval * 60 * 1000))
            val endDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

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
                    onDone.invoke()
                    Log.v("uploadSteps", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    Log.v("uploadSteps", t.message.toString())
                    onDone.invoke()
                }
            })
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            onDone.invoke()
        }
    }

    suspend fun uploadOxygen(context: Context, onDone: () -> Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow),
                )
            )
            var level = 0.0
            response.records.map {
                level = it.percentage.value
            }

            val currentTime = SimpleDateFormat(
                "yyyy-MM-dd mm:ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

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
                    onDone.invoke()
                    Log.v("uploadOxygen", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    Log.v("uploadOxygen", t.message.toString())
                    onDone.invoke()
                }
            })

        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            onDone.invoke()
        }
    }

    suspend fun updateBodyTemperature(context: Context, onDone: () -> Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = BodyTemperatureRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow),
                )
            )
            var temp = 0.0f
            response.records.map {
                temp = it.temperature.inCelsius.toFloat()
            }

            val currentTime = SimpleDateFormat(
                "yyyy-MM-dd mm:ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

            val call = WebService.getClient().insertClientBodyTemperatureRecord(
                temperature = temp,
                timeTemperatureTaken = currentTime,
                phoneNumber = Prefs.phone,
                zoneId = "America/New York"
            )

            call.enqueue(object : Callback<ResponseEntity> {
                override fun onResponse(
                    call: Call<ResponseEntity>,
                    response: Response<ResponseEntity>
                ) {
                    onDone.invoke()
                    Log.v("updateBodyTemperature", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    Log.v("updateBodyTemperature", t.message.toString())
                    onDone.invoke()
                }
            })
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            onDone.invoke()
        }
    }

    suspend fun updateRespiratoryRate(context: Context, onDone: () -> Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow),
                )
            )
            var rate = 0.0f
            response.records.map {
                rate = it.rate.toFloat()
            }

            val currentTime = SimpleDateFormat(
                "yyyy-MM-dd mm:ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

            val call = WebService.getClient().insertClientRespiratoryRateRecord(
                rate = rate,
                timeRespiratoryRateTaken = currentTime,
                phoneNumber = Prefs.phone,
                zoneId = "America/New York"
            )

            call.enqueue(object : Callback<ResponseEntity> {
                override fun onResponse(
                    call: Call<ResponseEntity>,
                    response: Response<ResponseEntity>
                ) {
                    onDone.invoke()
                    Log.v("updateRespiratoryRate", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    Log.v("updateRespiratoryRate", t.message.toString())
                    onDone.invoke()
                }
            })
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            onDone.invoke()
        }
    }

    suspend fun updateHeartRate(context: Context, onDone: () -> Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow),
                )
            )
            var rate = 0.0f
            response.records.map {
                rate = it.samples[0].beatsPerMinute.toFloat()
            }

            val currentTime = SimpleDateFormat(
                "yyyy-MM-dd mm:ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

            val call = WebService.getClient().insertClientHeartRateRecord(
                rate = rate,
                timeHeartRateTaken = currentTime,
                phoneNumber = Prefs.phone,
                zoneId = "America/New York"
            )

            call.enqueue(object : Callback<ResponseEntity> {
                override fun onResponse(
                    call: Call<ResponseEntity>,
                    response: Response<ResponseEntity>
                ) {
                    onDone.invoke()
                    Log.v("updateHeartRate", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    Log.v("updateHeartRate", t.message.toString())
                    onDone.invoke()
                }
            })
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            onDone.invoke()
        }
    }

    suspend fun updateDistanceRecord(context: Context, onDone: () -> Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = DistanceRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow),
                )
            )
            var distance = 0.0
            response.records.map {
                distance = it.distance.inMiles
            }
            val beginDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(startTime.toEpochMilli())

            val endDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

            val call = WebService.getClient().insertClientDistanceRecord(
                distanceInMiles = distance,
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
                    onDone.invoke()
                    Log.v("updateDistanceRecord", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    Log.v("updateDistanceRecord", t.message.toString())
                    onDone.invoke()
                }
            })
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            onDone.invoke()
        }
    }

    suspend fun updateBloodPressure(context: Context, onDone: () -> Unit) {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        Prefs.init(context)
        try {
            val startTime: Instant = Instant.now().minus(Prefs.interval, ChronoUnit.MINUTES)
            val timeNow: Instant = Instant.now()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = BloodPressureRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, timeNow),
                )
            )
            var systolic = 0
            var diastolic = 0
            response.records.map {
                systolic = it.systolic.inMillimetersOfMercury.toInt()
                diastolic = it.diastolic.inMillimetersOfMercury.toInt()
            }

            val currentTime = SimpleDateFormat(
                "yyyy-MM-dd mm:ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

            val endDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(System.currentTimeMillis())

            val call = WebService.getClient().insertClientBloodPressureRecord(
                systolic = systolic,
                diastolic = diastolic,
                timeBloodPressureTaken = currentTime,
//                endDate = endDate,
                phoneNumber = Prefs.phone,
                zoneId = "America/New York"
            )

            call.enqueue(object : Callback<ResponseEntity> {
                override fun onResponse(
                    call: Call<ResponseEntity>,
                    response: Response<ResponseEntity>
                ) {
                    onDone.invoke()
                    Log.v("updateBloodPressure", response.code().toString())
                }

                override fun onFailure(call: Call<ResponseEntity>, t: Throwable) {
                    Log.v("updateBloodPressure", t.message.toString())
                    onDone.invoke()
                }
            })
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            onDone.invoke()
        }
    }
}