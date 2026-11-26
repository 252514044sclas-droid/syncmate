package com.example.syncmate.data.remote

import com.example.syncmate.BuildConfig
import com.example.syncmate.data.local.AppPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    fun getBaseUrl(appPreferences: AppPreferences? = null): String {
        val custom = appPreferences?.customServerUrl
        if (!custom.isNullOrBlank()) {
            return if (custom.endsWith("/")) custom else "$custom/"
        }
        if (com.example.syncmate.BuildConfig.DEBUG) {
            // 127.0.0.1 connects instantly via adb reverse tcp:8000 tcp:8000
            // and avoids Windows Firewall dropping packets on 10.0.2.2
            return "http://127.0.0.1:8000/"
        }
        return BuildConfig.BASE_URL
    }

    fun getApiService(appPreferences: AppPreferences): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val resolvedBaseUrl = getBaseUrl(appPreferences)

        // Interceptor that seamlessly falls back between local hosts (e.g. 127.0.0.1 ADB reverse and 10.0.2.2 emulator router) in debug mode
        val hostFallbackInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            try {
                chain.proceed(originalRequest)
            } catch (e: IOException) {
                if (!BuildConfig.DEBUG) {
                    throw e
                }
                val currentHost = originalRequest.url.host
                val candidateHosts = listOf("127.0.0.1", "10.0.2.2")
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
            .connectTimeout(2500, TimeUnit.MILLISECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(resolvedBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
