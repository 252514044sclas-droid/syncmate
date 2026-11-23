package com.example.syncmate.data.remote

import com.example.syncmate.data.local.AppPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Primary URL for Physical Android Devices on local Wi-Fi network & Emulators
    // Machine IPv4: 172.23.176.202
    var pcIpAddress: String = "172.23.176.202"
    var baseUrl: String = "http://172.23.176.202:8000/"

    fun getApiService(appPreferences: AppPreferences): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Interceptor that seamlessly falls back between PC Wi-Fi IP, Emulator NAT (10.0.2.2), and ADB reverse (127.0.0.1)
        val hostFallbackInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            try {
                chain.proceed(originalRequest)
            } catch (e: IOException) {
                val currentHost = originalRequest.url.host
                val candidateHosts = listOf(pcIpAddress, "10.0.2.2", "127.0.0.1")
                val remainingHosts = candidateHosts.filter { it != currentHost }

                var lastException: IOException = e
                for (fallbackHost in remainingHosts) {
                    try {
                        val fallbackUrl = originalRequest.url.newBuilder().host(fallbackHost).build()
                        val fallbackRequest = originalRequest.newBuilder().url(fallbackUrl).build()
                        return@Interceptor chain.proceed(fallbackRequest)
                    } catch (ex: IOException) {
                        lastException = ex
                    }
                }
                throw lastException
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(hostFallbackInterceptor)
            .addInterceptor(AuthInterceptor(appPreferences))
            .addInterceptor(logging)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
