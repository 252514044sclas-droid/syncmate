package com.example.syncmate.data.remote

import com.example.syncmate.data.local.AppPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class AuthInterceptor(private val appPreferences: AppPreferences? = null) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val user = FirebaseAuth.getInstance().currentUser
        val token = if (user != null) {
            try {
                Tasks.await(user.getIdToken(false), 5, TimeUnit.SECONDS)?.token
            } catch (_: Exception) {
                null
            }
        } else if (com.example.syncmate.BuildConfig.DEBUG) {
            "dev_debug_token"
        } else {
            null
        }

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        requestBuilder.header("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}
