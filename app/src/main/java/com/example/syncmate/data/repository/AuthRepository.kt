package com.example.syncmate.data.repository

import com.example.syncmate.data.local.AppPreferences
import com.example.syncmate.data.model.User
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.ForgotPasswordRequestDto
import com.example.syncmate.data.remote.dto.LoginRequestDto
import com.example.syncmate.data.remote.dto.RegisterRequestDto
import com.example.syncmate.data.remote.dto.UserDto
import com.example.syncmate.data.remote.dto.VerifyEmailRequestDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthRepository {
    val currentUserFlow: Flow<User?>
    suspend fun login(email: String, pass: String): Result<User>
    suspend fun signUp(name: String, email: String, pass: String, university: String): Result<User>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun verifyEmail(email: String, code: String): Result<Boolean>
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}

class DevAuthRepositoryImpl(
    private val apiService: ApiService? = null,
    private val appPreferences: AppPreferences? = null
) : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "usr_dev_101",
            name = "Alex Morgan",
            email = "alex.morgan@university.edu",
            university = "National Institute of Technology",
            isVerified = true,
            bio = "Computer Science student looking for summer sublet near campus.",
            preferredLeaseTerm = "May - Aug 2025",
            maxBudget = 15000.0,
            preferredLocation = "Adyar / IIT Campus"
        )
    )
    override val currentUserFlow: Flow<User?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, pass: String): Result<User> {
        if (apiService != null) {
            try {
                val res = apiService.login(LoginRequestDto(email, pass))
                if (res.isSuccessful && res.body()?.data != null) {
                    val authData = res.body()!!.data!!
                    val token = authData.token ?: "token_dev_sample"
                    appPreferences?.authToken = token
                    appPreferences?.isLoggedIn = true
                    appPreferences?.userEmail = email

                    val user = authData.user?.toDomain() ?: User(
                        id = "usr_" + email.hashCode(),
                        name = email.substringBefore("@").replace(".", " ").capitalize(),
                        email = email,
                        university = "National Institute of Technology",
                        isVerified = true
                    )
                    _currentUser.value = user
                    return Result.success(user)
                }
            } catch (_: Exception) {
                // Network unavailable -> fallback to local mock login
            }
        }

        delay(800)
        return if (email.contains("@") && pass.length >= 6) {
            val user = User(
                id = "usr_" + email.hashCode(),
                name = email.substringBefore("@").replace(".", " ").capitalize(),
                email = email,
                university = "National Institute of Technology",
                isVerified = email.endsWith(".edu")
            )
            appPreferences?.isLoggedIn = true
            appPreferences?.userEmail = email
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Invalid credentials. Please enter a valid student email and password."))
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        pass: String,
        university: String
    ): Result<User> {
        if (apiService != null) {
            try {
                val res = apiService.register(RegisterRequestDto(name, email, pass, university))
                if (res.isSuccessful && res.body()?.data != null) {
                    val authData = res.body()!!.data!!
                    appPreferences?.authToken = authData.token
                    appPreferences?.isLoggedIn = true

                    val user = authData.user?.toDomain() ?: User(
                        id = "usr_" + System.currentTimeMillis(),
                        name = name,
                        email = email,
                        university = university,
                        isVerified = email.endsWith(".edu")
                    )
                    _currentUser.value = user
                    return Result.success(user)
                }
            } catch (_: Exception) {
                // Fallback to local dev registration
            }
        }

        delay(900)
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Invalid email format"))
        if (pass.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))

        val newUser = User(
            id = "usr_" + System.currentTimeMillis(),
            name = name,
            email = email,
            university = university,
            isVerified = email.endsWith(".edu")
        )
        appPreferences?.isLoggedIn = true
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        if (apiService != null) {
            try {
                val res = apiService.forgotPassword(ForgotPasswordRequestDto(email))
                if (res.isSuccessful) return Result.success(Unit)
            } catch (_: Exception) {}
        }
        delay(600)
        return if (email.contains("@")) Result.success(Unit) else Result.failure(IllegalArgumentException("Invalid email address"))
    }

    override suspend fun verifyEmail(email: String, code: String): Result<Boolean> {
        if (apiService != null) {
            try {
                val res = apiService.verifyEmail(VerifyEmailRequestDto(email, code))
                if (res.isSuccessful && res.body()?.data == true) {
                    _currentUser.value = _currentUser.value?.copy(isVerified = true)
                    return Result.success(true)
                }
            } catch (_: Exception) {}
        }
        delay(600)
        return if (code.length == 6) {
            _currentUser.value = _currentUser.value?.copy(isVerified = true)
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Verification code must be 6 digits"))
        }
    }

    override suspend fun signOut() {
        appPreferences?.clear()
        _currentUser.value = null
    }

    override fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null || appPreferences?.isLoggedIn == true
    }

    private fun UserDto.toDomain() = User(
        id = id,
        name = name,
        email = email,
        university = university,
        isVerified = isVerified,
        profilePictureUrl = profilePictureUrl,
        bio = bio,
        preferredLeaseTerm = preferredLeaseTerm,
        maxBudget = maxBudget,
        preferredLocation = preferredLocation
    )
}
