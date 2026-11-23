package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PreferenceVectorDto(
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("university") val university: String? = null,
    @SerializedName("sleep_schedule") val sleepSchedule: String? = null,
    @SerializedName("cleanliness_rating") val cleanlinessRating: Int? = null,
    @SerializedName("guest_policy") val guestPolicy: String? = null,
    @SerializedName("noise_tolerance") val noiseTolerance: String? = null,
    @SerializedName("study_habits") val studyHabits: String? = null,
    @SerializedName("dietary_preference") val dietaryPreference: String? = null,
    @SerializedName("smoking_option") val smokingOption: Boolean? = null,
    @SerializedName("pet_preference") val petPreference: String? = null,
    @SerializedName("sharing_style") val sharingStyle: String? = null,
    @SerializedName("max_budget_inr") val maxBudgetINR: Double? = null,
    @SerializedName("preferred_location") val preferredLocation: String? = null
)

data class PredictCompatibilityRequestDto(
    @SerializedName("user_1") val user1: PreferenceVectorDto,
    @SerializedName("user_2") val user2: PreferenceVectorDto
)

data class CompatibilityDimensionScoresDto(
    @SerializedName("sleep_rhythm") val sleepRhythm: Int = 85,
    @SerializedName("cleanliness") val cleanliness: Int = 85,
    @SerializedName("study_noise") val studyNoise: Int = 85,
    @SerializedName("dietary") val dietary: Int = 85,
    @SerializedName("guest_policy") val guestPolicy: Int = 85,
    @SerializedName("smoking_pets") val smokingPets: Int = 85,
    @SerializedName("sharing_style") val sharingStyle: Int = 85,
    @SerializedName("budget_overlap") val budgetOverlap: Int = 85
)

data class CompatibilityResultDto(
    @SerializedName("compatibility_percentage") val compatibilityPercentage: Int = 85,
    @SerializedName("match_reason") val matchReason: String = "",
    @SerializedName("why_you_match") val whyYouMatch: List<String> = emptyList(),
    @SerializedName("dimension_scores") val dimensionScores: CompatibilityDimensionScoresDto = CompatibilityDimensionScoresDto()
)
