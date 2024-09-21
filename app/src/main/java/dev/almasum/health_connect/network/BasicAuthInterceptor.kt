package dev.almasum.health_connect.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Base64

class BasicAuthInterceptor(user: String, password: String) : Interceptor {
    private val credentials: String = "Basic " + Base64.getEncoder().encodeToString("$user:$password".toByteArray())

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
            .newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        return chain.proceed(request)
    }
}
