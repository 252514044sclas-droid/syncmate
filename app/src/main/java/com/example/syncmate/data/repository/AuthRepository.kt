package com.example.syncmate.data.repository

import com.example.syncmate.data.model.User
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

class DevAuthRepositoryImpl : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "usr_dev_101",
            name = "Alex Morgan",
            email = "alex.morgan@university.edu",
            university = "State University",
            isVerified = true,
            bio = "Computer Science Junior looking for summer sublet near campus.",
            preferredLeaseTerm = "May - Aug 2025",
            maxBudget = 1100.0,
            preferredLocation = "North Campus"
        )
    )
    override val currentUserFlow: Flow<User?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, pass: String): Result<User> {
        delay(1000)
        return if (email.contains("@") && pass.length >= 6) {
            val user = User(
                id = "usr_" + email.hashCode(),
                name = email.substringBefore("@").replace(".", " ").capitalize(),
                email = email,
                university = "State University",
                isVerified = email.endsWith(".edu")
            )
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
        delay(1200)
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Invalid email format"))
        if (pass.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))

        val newUser = User(
            id = "usr_" + System.currentTimeMillis(),
            name = name,
            email = email,
            university = university,
            isVerified = email.endsWith(".edu")
        )
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        delay(800)
        return if (email.contains("@")) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Please provide a valid email address"))
        }
    }

    override suspend fun verifyEmail(email: String, code: String): Result<Boolean> {
        delay(800)
        return if (code.length == 6) {
            _currentUser.value = _currentUser.value?.copy(isVerified = true)
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Verification code must be 6 digits"))
        }
    }

    override suspend fun signOut() {
        delay(400)
        _currentUser.value = null
    }

    override fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}
