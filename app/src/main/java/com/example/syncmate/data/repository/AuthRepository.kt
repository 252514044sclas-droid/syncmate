package com.example.syncmate.data.repository

import com.example.syncmate.data.local.AppPreferences
import com.example.syncmate.data.model.User
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.util.PasswordValidator
import com.example.syncmate.data.util.UniversityDomains
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
    suspend fun deleteAccount(): Result<Unit>
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val appPreferences: AppPreferences? = null
) : AuthRepository {

    // Compatibility constructor for AppNavGraph injection
    constructor(apiService: ApiService?, appPreferences: AppPreferences?) : this(
        FirebaseAuth.getInstance(),
        appPreferences
    )

    private val devUser = kotlinx.coroutines.flow.MutableStateFlow<User?>(
        if (com.example.syncmate.BuildConfig.DEBUG && appPreferences?.savedUserId != null && appPreferences.userEmail != null) {
            User(
                id = appPreferences.savedUserId!!,
                name = appPreferences.savedName ?: "Student",
                email = appPreferences.userEmail!!,
                university = appPreferences.savedUniversity ?: "SRM Institute of Science and Technology",
                isVerified = true,
                bio = appPreferences.savedBio ?: "Verified student looking for sublets and roommates.",
                hasCompletedQuiz = true
            )
        } else null
    )

    override val currentUserFlow: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                if (devUser.value == null) {
                    trySend(null)
                }
            } else {
                val email = firebaseUser.email ?: ""
                val isVerified = firebaseUser.isEmailVerified && UniversityDomains.isAllowedUniversityDomain(email)
                val immediateUser = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                    email = email,
                    university = "University",
                    isVerified = isVerified,
                    bio = "Student looking for verified housing and roommates.",
                    hasCompletedQuiz = false
                )
                trySend(immediateUser)

                launch(Dispatchers.IO) {
                    val firestoreUser = com.example.syncmate.data.firestore.SyncMateFirestore.getUserAsync(firebaseUser.uid)
                    if (firestoreUser != null) {
                        trySend(firestoreUser.copy(isVerified = isVerified))
                    }
                }
            }
        }
        firebaseAuth.addAuthStateListener(listener)

        val devJob = launch {
            devUser.collect { dUser ->
                if (firebaseAuth.currentUser == null) {
                    trySend(dUser)
                }
            }
        }

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
            devJob.cancel()
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null || (com.example.syncmate.BuildConfig.DEBUG && devUser.value != null)
    }

    override suspend fun login(email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid student email address."))
        }
        if (pass.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Password cannot be empty."))
        }

        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(trimmedEmail, pass).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("User authentication failed.")

            // Refresh email verification state
            try { firebaseUser.reload().await() } catch (_: Exception) {}

            val isVerified = firebaseUser.isEmailVerified && UniversityDomains.isAllowedUniversityDomain(trimmedEmail)
            val existing = com.example.syncmate.data.firestore.SyncMateFirestore.getUserAsync(firebaseUser.uid)
            val user = (existing ?: User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: trimmedEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                email = trimmedEmail,
                university = "University",
                isVerified = isVerified,
                bio = "Student looking for verified sublets and compatible roommates.",
                hasCompletedQuiz = false
            )).copy(isVerified = isVerified)

            appPreferences?.savedUserId = user.id
            appPreferences?.userEmail = user.email
            appPreferences?.savedName = user.name
            appPreferences?.savedUniversity = user.university
            com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            if (com.example.syncmate.BuildConfig.DEBUG && isInvalidApiKeyError(e)) {
                val devUid = "dev_" + trimmedEmail.hashCode().toString().replace("-", "")
                val isVerified = UniversityDomains.isAllowedUniversityDomain(trimmedEmail)
                val uniName = if (trimmedEmail.contains("srm", ignoreCase = true)) {
                    "SRM Institute of Science and Technology"
                } else if (trimmedEmail.contains("iit", ignoreCase = true)) {
                    "IIT Madras"
                } else if (trimmedEmail.contains("anna", ignoreCase = true)) {
                    "Anna University"
                } else {
                    "University"
                }
                val user = User(
                    id = devUid,
                    name = trimmedEmail.substringBefore("@").replace(".", " ").split(" ")
                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                    email = trimmedEmail,
                    university = uniName,
                    isVerified = isVerified,
                    bio = "Verified student looking for sublets and compatible roommates.",
                    hasCompletedQuiz = true
                )
                appPreferences?.savedUserId = devUid
                appPreferences?.userEmail = trimmedEmail
                appPreferences?.savedName = user.name
                appPreferences?.savedUniversity = user.university
                devUser.value = user
                com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(user)
                return@withContext Result.success(user)
            }
            Result.failure(mapFirebaseAuthError(e))
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        pass: String,
        university: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedUni = university.trim()

        if (trimmedName.isBlank()) return@withContext Result.failure(IllegalArgumentException("Please enter your full name."))
        if (trimmedUni.isBlank()) return@withContext Result.failure(IllegalArgumentException("Please enter your university name."))
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid student email address."))
        }

        // Enforce password strength validation
        val passwordCheck = PasswordValidator.validate(pass)
        if (passwordCheck.isFailure) {
            return@withContext Result.failure(passwordCheck.exceptionOrNull()!!)
        }

        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, pass).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Registration failed.")

            // Update display name in Firebase Auth
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(trimmedName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
            } catch (_: Exception) {}

            // Send real email verification
            try {
                firebaseUser.sendEmailVerification().await()
            } catch (_: Exception) {}

            val isVerifiedStudent = firebaseUser.isEmailVerified && UniversityDomains.isAllowedUniversityDomain(trimmedEmail)

            val newUser = User(
                id = firebaseUser.uid, // REAL FIREBASE UID
                name = trimmedName,
                email = trimmedEmail,
                university = trimmedUni,
                isVerified = isVerifiedStudent,
                bio = "Student at $trimmedUni looking for verified housing and roommates.",
                hasCompletedQuiz = false
            )

            appPreferences?.savedUserId = newUser.id
            appPreferences?.userEmail = newUser.email
            appPreferences?.savedName = newUser.name
            appPreferences?.savedUniversity = newUser.university
            com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(newUser)
            Result.success(newUser)
        } catch (e: Exception) {
            if (com.example.syncmate.BuildConfig.DEBUG && isInvalidApiKeyError(e)) {
                val devUid = "dev_" + trimmedEmail.hashCode().toString().replace("-", "")
                val isVerified = UniversityDomains.isAllowedUniversityDomain(trimmedEmail)
                val newUser = User(
                    id = devUid,
                    name = trimmedName,
                    email = trimmedEmail,
                    university = trimmedUni,
                    isVerified = isVerified,
                    bio = "Student at $trimmedUni looking for verified housing and roommates.",
                    hasCompletedQuiz = true
                )
                appPreferences?.savedUserId = devUid
                appPreferences?.userEmail = trimmedEmail
                appPreferences?.savedName = newUser.name
                appPreferences?.savedUniversity = newUser.university
                devUser.value = newUser
                com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(newUser)
                return@withContext Result.success(newUser)
            }
            Result.failure(mapFirebaseAuthError(e))
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        try {
            firebaseAuth.sendPasswordResetEmail(trimmedEmail).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapFirebaseAuthError(e))
        }
    }

    override suspend fun verifyEmail(email: String, code: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val firebaseUser = firebaseAuth.currentUser
            ?: return@withContext Result.failure(IllegalStateException("No authenticated user found. Please log in first."))

        try {
            firebaseUser.reload().await()
            if (firebaseUser.isEmailVerified) {
                val isStudent = UniversityDomains.isAllowedUniversityDomain(firebaseUser.email ?: "")
                val existing = com.example.syncmate.data.firestore.SyncMateFirestore.getUserAsync(firebaseUser.uid)
                if (existing != null) {
                    com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(existing.copy(isVerified = isStudent))
                }
                Result.success(true)
            } else {
                // Send or resend verification email link
                try { firebaseUser.sendEmailVerification().await() } catch (_: Exception) {}
                Result.failure(Exception("Verification link sent to ${firebaseUser.email}. Please click the link in your email and tap Verify again."))
            }
        } catch (e: Exception) {
            Result.failure(mapFirebaseAuthError(e))
        }
    }

    override suspend fun updateProfile(
        name: String,
        university: String,
        bio: String,
        preferredLeaseTerm: String?,
        maxBudget: Double?,
        preferredLocation: String?
    ): Result<User> = withContext(Dispatchers.IO) {
        val firebaseUser = firebaseAuth.currentUser
            ?: return@withContext Result.failure(IllegalStateException("User is not logged in."))

        val uid = firebaseUser.uid
        val existing = com.example.syncmate.data.firestore.SyncMateFirestore.getUserAsync(uid)
        val isVerified = firebaseUser.isEmailVerified && UniversityDomains.isAllowedUniversityDomain(firebaseUser.email ?: "")

        val updatedUser = (existing ?: User(
            id = uid,
            name = name.trim(),
            email = firebaseUser.email ?: "",
            university = university.trim(),
            isVerified = isVerified,
            bio = bio.trim(),
            hasCompletedQuiz = true
        )).copy(
            name = name.trim(),
            university = university.trim(),
            bio = bio.trim(),
            preferredLeaseTerm = preferredLeaseTerm ?: existing?.preferredLeaseTerm ?: "Summer 2026",
            maxBudget = maxBudget ?: existing?.maxBudget ?: 15000.0,
            preferredLocation = preferredLocation ?: existing?.preferredLocation ?: "Near Campus / Metro",
            hasCompletedQuiz = true
        )

        // Also update Firebase Auth display name if changed
        if (firebaseUser.displayName != name.trim()) {
            try {
                val updates = UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()
                firebaseUser.updateProfile(updates).await()
            } catch (_: Exception) {}
        }

        com.example.syncmate.data.firestore.SyncMateFirestore.saveUser(updatedUser)
        Result.success(updatedUser)
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            if (com.example.syncmate.BuildConfig.DEBUG && devUser.value != null) {
                val uid = devUser.value!!.id
                try {
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    db.collection("users").document(uid).delete().await()
                } catch (_: Exception) {}
                devUser.value = null
                appPreferences?.clear()
                return@withContext Result.success(Unit)
            }
            return@withContext Result.failure(Exception("No authenticated user"))
        }
        val uid = user.uid
        try {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("users").document(uid).collection("private").document("contact").delete().await()
                db.collection("users").document(uid).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            devUser.value = null
            appPreferences?.clear()
            user.delete().await()
            Result.success(Unit)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(Exception("Deleting your account is sensitive and requires recent authentication. Please sign out and sign in again before deleting."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to delete account"))
        }
    }

    override suspend fun signOut(): Unit = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signOut()
            devUser.value = null
            appPreferences?.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isInvalidApiKeyError(e: Exception): Boolean {
        val msg = e.message ?: e.localizedMessage ?: ""
        return msg.contains("API key not valid", ignoreCase = true) ||
               msg.contains("API_KEY_INVALID", ignoreCase = true) ||
               msg.contains("AIzaSyFakeKey", ignoreCase = true)
    }

    private fun mapFirebaseAuthError(e: Exception): Exception {
        if (isInvalidApiKeyError(e)) {
            return Exception("Firebase API Key is invalid. Please replace app/google-services.json with your actual project file from Firebase Console.")
        }
        return when (e) {
            is FirebaseAuthInvalidUserException ->
                Exception("No account found with this email. Please check your email or sign up.")
            is FirebaseAuthInvalidCredentialsException ->
                Exception("Incorrect password or invalid email format. Please try again.")
            is FirebaseAuthUserCollisionException ->
                Exception("An account already exists with this email address. Please log in.")
            is FirebaseAuthWeakPasswordException ->
                Exception("The password provided is too weak. Please use at least 8 characters with letters and numbers.")
            is FirebaseNetworkException ->
                Exception("Network error: Unable to connect to Firebase. Please check your internet connection.")
            is FirebaseAuthRecentLoginRequiredException ->
                Exception("This action is sensitive and requires recent authentication. Please log out and log in again.")
            is FirebaseAuthException ->
                Exception(e.localizedMessage ?: "Authentication failed: ${e.errorCode}")
            else ->
                Exception(e.localizedMessage ?: "An unexpected error occurred. Please try again.")
        }
    }
}

// Backward-compatible alias
typealias DevAuthRepositoryImpl = FirebaseAuthRepositoryImpl

private suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result -> cont.resume(result) }
        addOnFailureListener { exception -> cont.resumeWithException(exception) }
        addOnCanceledListener { cont.cancel() }
    }
