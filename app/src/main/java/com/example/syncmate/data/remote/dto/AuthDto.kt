package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("university") val university: String
)

data class VerifyEmailRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class ForgotPasswordRequestDto(
    @SerializedName("email") val email: String
)

data class UpdateProfileRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("university") val university: String,
    @SerializedName("bio") val bio: String,
    @SerializedName("preferred_lease_term") val preferredLeaseTerm: String? = null,
    @SerializedName("max_budget") val maxBudget: Double? = null,
    @SerializedName("preferred_location") val preferredLocation: String? = null
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("university") val university: String,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerializedName("bio") val bio: String = "",
    @SerializedName("preferred_lease_term") val preferredLeaseTerm: String = "Summer 2025",
    @SerializedName("max_budget") val maxBudget: Double = 15000.0,
    @SerializedName("preferred_location") val preferredLocation: String = "Adyar / IIT Campus"
)

data class AuthResponseDto(
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserDto? = null
)
