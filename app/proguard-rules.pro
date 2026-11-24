# =================================================================
# SyncMate ProGuard / R8 Rules for Release Builds
# =================================================================

# --- Retrofit 2 ---
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# --- OkHttp 3 & Okio ---
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }

# --- Gson & DTO Models ---
-keepattributes EnclosingMethod
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }
-keep class com.example.syncmate.data.remote.dto.** { *; }
-keep class com.example.syncmate.data.model.** { *; }

# --- Room Database ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.example.syncmate.data.local.room.** { *; }
-keepclassmembers class com.example.syncmate.data.local.room.** { *; }

# --- Firebase (Firestore & Auth) ---
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# --- Kotlin Coroutines ---
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

# --- AndroidX Security & Crypto ---
-keep class androidx.security.crypto.** { *; }

# --- Jetpack Compose ---
-keep class androidx.compose.** { *; }
