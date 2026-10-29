package com.example.syncmate.data.remote

import com.example.syncmate.data.local.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val appPreferences: AppPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = appPreferences.authToken

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        requestBuilder.header("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}
