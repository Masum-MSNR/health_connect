package dev.almasum.health_connect.network

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dev.almasum.health_connect.network.pojo.ResponseEntity
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query
import java.lang.reflect.Type


interface WebService {
    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }

    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder().registerTypeAdapter(
                SpannableString::class.java, SpannableDeserializer()
            )
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        private var httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("hb-tst1-api.ogenushealth.com")
            .build()

        fun getClient(): WebService = create(httpUrl)
        private fun create(httpUrl: HttpUrl): WebService {
            val client = OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor("healthbridge_app_user2", "SanDiegoZoo2021!"))
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(WebService::class.java)
        }
    }

    @POST("/createClient")
    fun createClient(
        @Query("firstName") firstName: String,
        @Query("lastName") lastName: String,
        @Query("phone") phone: String,
        @Query("email") email: String,
        @Query("phoneType") phoneType: String,
        @Query("providerId") providerId: Int
    ): Call<ResponseEntity>

    @POST("/insertClientStepsRecord")
    fun insertClientStepsRecord(
        @Query("steps") steps: Int,
        @Query("beginDate") beginDate: String,
        @Query("endDate") endDate: String,
        @Query("phoneNumber") phoneNumber: String,
        @Query("zoneId") zoneId: String
    ): Call<ResponseEntity>

    @POST("/insertClientOxygenSaturationRecord")
    fun insertClientOxygenSaturationRecord(
        @Query("oxygenSaturationPercent") oxygenSaturationPercent: Double,
        @Query("timeOxygenRecordTaken") timeOxygenRecordTaken: String,
        @Query("phoneNumber") phoneNumber: String,
        @Query("zoneId") zoneId: String
    ): Call<ResponseEntity>
}