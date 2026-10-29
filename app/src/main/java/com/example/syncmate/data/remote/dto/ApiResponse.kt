package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status") val status: String, // "success" or "error"
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)
