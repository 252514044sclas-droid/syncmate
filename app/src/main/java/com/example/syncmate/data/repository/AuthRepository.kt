package com.example.syncmate.data.repository

import com.example.syncmate.data.local.AppPreferences
import com.example.syncmate.data.model.User
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.ForgotPasswordRequestDto
import com.example.syncmate.data.remote.dto.LoginRequestDto
import com.example.syncmate.data.remote.dto.RegisterRequestDto
import com.example.syncmate.data.remote.dto.UpdateProfileRequestDto
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
    suspend fun updateProfile(
        name: String,
        university: String,
        bio: String,
        preferredLeaseTerm: String? = null,
        maxBudget: Double? = null,
        preferredLocation: String? = null
    ): Result<User>
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}

class DevAuthRepositoryImpl(
    private val apiService: ApiService? = null,
    private val appPreferences: AppPreferences? = null
) : AuthRepository {

    private val initialUser = if (appPreferences?.isLoggedIn == true) {
        User(
            id = "usr_dev_101",
            name = appPreferences.savedName ?: "Alex Morgan",
            email = appPreferences.userEmail ?: "alex.morgan@university.edu",
            university = appPreferences.savedUniversity ?: "National Institute of Technology",
            isVerified = true,
            bio = appPreferences.savedBio ?: "Computer Science student looking for summer sublet near campus.",
            preferredLeaseTerm = appPreferences.savedPreferredLease ?: "Summer 2025",
            maxBudget = appPreferences.savedMaxBudget,
            preferredLocation = appPreferences.savedPreferredLocation ?: "Adyar / IIT Campus",
            hasCompletedQuiz = true
        )
    } else {
        null
    }

    private val _currentUser = MutableStateFlow<User?>(initialUser)
    override val currentUserFlow: Flow<User?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, pass: String): Result<User> {
        delay(300)
        if (!email.contains("@") || pass.length < 6) {
            return Result.failure(IllegalArgumentException("Invalid credentials. Please enter a valid student email and password (min 6 characters)."))
        }

        val userId = "usr_" + kotlin.math.abs(email.hashCode())
        val existing = com.example.syncmate.data.firestore.SyncMateFirestore.getUser(userId)

        val user = existing ?: User(
            id = userId,
            name = appPreferences?.savedName ?: email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
            email = email.trim(),
            university = appPreferences?.savedUniversity ?: "National Institute of Technology",
            isVerified = true,
            bio = appPreferences?.savedBio ?: "Student looking for verified sublets and compatible roommates.",
            hasCompletedQuiz = false
        )

        appPreferences?.isLoggedIn = true
        appPreferences?.userEmail = email
        appPreferences?.savedName = user.name
        appPreferences?.savedUniversity = user.university
        appPreferences?.authToken = "auth_token_${user.id}"

        com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(user)
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun signUp(
        name: String,
        email: String,
        pass: String,
        university: String
    ): Result<User> {
        delay(300)
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Please enter a valid student email address."))
        if (pass.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Please enter your full name."))
        if (university.isBlank()) return Result.failure(IllegalArgumentException("Please enter your university name."))

        val userId = "usr_" + kotlin.math.abs(email.hashCode())
        val isVerifiedStudent = email.endsWith(".edu") || email.contains("srm") || email.contains("iit") || email.contains("anna") || email.contains("du") || email.contains("univ")

        val newUser = User(
            id = userId,
            name = name.trim(),
            email = email.trim(),
            university = university.trim(),
            isVerified = isVerifiedStudent,
            bio = "Student at ${university.trim()} looking for verified housing and roommates.",
            hasCompletedQuiz = false
        )

        appPreferences?.isLoggedIn = true
        appPreferences?.userEmail = email.trim()
        appPreferences?.savedName = newUser.name
        appPreferences?.savedUniversity = newUser.university
        appPreferences?.authToken = "auth_token_${newUser.id}"

        // Save directly to Firestore users collection
        com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(newUser)
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        delay(300)
        return if (email.contains("@")) Result.success(Unit) else Result.failure(IllegalArgumentException("Invalid email address"))
    }

    override suspend fun verifyEmail(email: String, code: String): Result<Boolean> {
        delay(300)
        return if (code.length == 6) {
            _currentUser.value = _currentUser.value?.copy(isVerified = true)
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Verification code must be 6 digits"))
        }
    }

    override suspend fun updateProfile(
        name: String,
        university: String,
        bio: String,
        preferredLeaseTerm: String?,
        maxBudget: Double?,
        preferredLocation: String?
    ): Result<User> {
        delay(300)
        val updatedUser = _currentUser.value?.copy(
            name = name.trim(),
            university = university.trim(),
            bio = bio.trim(),
            preferredLeaseTerm = preferredLeaseTerm ?: (_currentUser.value?.preferredLeaseTerm ?: "Summer 2025"),
            maxBudget = maxBudget ?: (_currentUser.value?.maxBudget ?: 15000.0),
            preferredLocation = preferredLocation ?: (_currentUser.value?.preferredLocation ?: "Near Campus / Metro"),
            hasCompletedQuiz = true
        ) ?: User(
            id = "usr_dev_101",
            name = name.trim(),
            email = appPreferences?.userEmail ?: "student@university.edu",
            university = university.trim(),
            isVerified = true,
            bio = bio.trim(),
            hasCompletedQuiz = true
        )

        appPreferences?.savedName = updatedUser.name
        appPreferences?.savedUniversity = updatedUser.university
        appPreferences?.savedBio = updatedUser.bio
        appPreferences?.savedPreferredLease = updatedUser.preferredLeaseTerm
        appPreferences?.savedMaxBudget = updatedUser.maxBudget
        appPreferences?.savedPreferredLocation = updatedUser.preferredLocation

        com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(updatedUser)
        _currentUser.value = updatedUser
        return Result.success(updatedUser)
    }

    override suspend fun signOut() {
        appPreferences?.clear()
        _currentUser.value = null
    }

    override fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null || appPreferences?.isLoggedIn == true
    }

    private fun UserDto.toDomain() = User(
        id = id ?: ("usr_" + System.currentTimeMillis()),
        name = name ?: "Student",
        email = email ?: "student@university.edu",
        university = university ?: "University",
        isVerified = isVerified ?: false,
        profilePictureUrl = profilePictureUrl,
        bio = bio ?: "",
        preferredLeaseTerm = preferredLeaseTerm ?: "Summer 2025",
        maxBudget = maxBudget ?: 15000.0,
        preferredLocation = preferredLocation ?: "Near Campus / Metro",
        hasCompletedQuiz = false
    )
}
