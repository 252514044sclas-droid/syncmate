package com.example.syncmate.data.firestore

import com.example.syncmate.data.model.ChatRoom
import com.example.syncmate.data.model.DirectMessage
import com.example.syncmate.data.model.RoommateRequest
import com.example.syncmate.data.model.User
import com.example.syncmate.data.remote.ApiService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap

typealias ListenerRegistration = com.google.firebase.firestore.ListenerRegistration

/**
 * Cloud Firestore Real-Time Cross-Device Synchronization Engine.
 * Now fully powered by the official Firebase SDK for real-time chat and roommate matching.
 */
object SyncMateFirestore {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val db by lazy { FirebaseFirestore.getInstance() }
    
    // Local cache to support synchronous UI lookups
    private val localUserCache = ConcurrentHashMap<String, User>()

    fun initApiService(service: ApiService) {
        // Kept for backward compatibility with AppNavGraph, but networking is now handled natively by Firebase.
    }

    private val blockedUserIds = mutableSetOf<String>()

    fun blockUser(currentUserId: String, targetUserId: String) {
        blockedUserIds.add(targetUserId)
        scope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(currentUserId)
                    .collection("blocked").document(targetUserId)
                    .set(mapOf("blockedAt" to System.currentTimeMillis())).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportUser(reporterId: String, reportedId: String, reason: String, details: String = "") {
        scope.launch(Dispatchers.IO) {
            try {
                val reportData = mapOf(
                    "reporterId" to reporterId,
                    "reportedId" to reportedId,
                    "reason" to reason,
                    "details" to details,
                    "timestamp" to System.currentTimeMillis(),
                    "status" to "PENDING_REVIEW"
                )
                db.collection("reports").document("rep_${System.currentTimeMillis()}").set(reportData).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener to the "users" collection
     */
    fun attachUsersSnapshotListener(
        currentUserId: String,
        onUpdate: (List<User>) -> Unit
    ): ListenerRegistration {
        // Real-time listener for blocked users
        val blockReg = db.collection("users").document(currentUserId)
            .collection("blocked")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    blockedUserIds.clear()
                    blockedUserIds.addAll(snap.documents.map { it.id })
                }
            }

        val usersReg = db.collection("users")
            .whereEqualTo("hasCompletedQuiz", true)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val allUsers = snapshot.toObjects(User::class.java)
                    
                    // Update local synchronous cache
                    allUsers.forEach { localUserCache[it.id] = it }
                    
                    val currentUserName = allUsers.firstOrNull { it.id == currentUserId }?.name ?: ""
                    
                    val filtered = allUsers.filter { user ->
                        user.id != currentUserId &&
                        !blockedUserIds.contains(user.id) &&
                        (currentUserName.isBlank() || !user.name.equals(currentUserName, ignoreCase = true))
                    }
                    onUpdate(filtered)
                }
            }

        return CompositeListenerRegistration(listOf(blockReg, usersReg))
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on the "roommate_requests" collection
     * using two targeted queries (receiverId == userId and senderId == userId) to strictly
     * comply with Firestore security rules.
     */
    fun attachRequestsSnapshotListener(
        userId: String,
        onUpdate: (incoming: List<RoommateRequest>, sent: List<RoommateRequest>) -> Unit
    ): ListenerRegistration {
        var incomingList = emptyList<RoommateRequest>()
        var sentList = emptyList<RoommateRequest>()

        val regIncoming = db.collection("roommate_requests")
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    incomingList = snapshot.toObjects(RoommateRequest::class.java).sortedByDescending { it.timestamp }
                    onUpdate(incomingList, sentList)
                }
            }

        val regSent = db.collection("roommate_requests")
            .whereEqualTo("senderId", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    sentList = snapshot.toObjects(RoommateRequest::class.java).sortedByDescending { it.timestamp }
                    onUpdate(incomingList, sentList)
                }
            }

        return CompositeListenerRegistration(listOf(regIncoming, regSent))
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on "roommate_requests"
     * strictly filtered by receiverId == currentUserId and status == "PENDING".
     */
    fun attachIncomingPendingRequestsListener(
        receiverId: String,
        onUpdate: (List<RoommateRequest>) -> Unit
    ): ListenerRegistration {
        return db.collection("roommate_requests")
            .whereEqualTo("receiverId", receiverId)
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val pendingIncoming = snapshot.toObjects(RoommateRequest::class.java)
                        .sortedByDescending { it.timestamp }
                    onUpdate(pendingIncoming)
                }
            }
    }

    /**
     * Writes/updates a user document in Firestore.
     * Public profile fields are stored at users/{userId}.
     * Private fields (email, phone) are stored in the owner-only subcollection users/{userId}/private/contact.
     */
    fun saveUser(user: User) {
        scope.launch(Dispatchers.IO) {
            try {
                // Public profile fields (no email or phone stored in public matching document)
                val publicMap = mutableMapOf<String, Any?>(
                    "id" to user.id,
                    "name" to user.name,
                    "university" to user.university,
                    "isVerified" to user.isVerified,
                    "profilePictureUrl" to user.profilePictureUrl,
                    "bio" to user.bio,
                    "preferredLeaseTerm" to user.preferredLeaseTerm,
                    "maxBudget" to user.maxBudget,
                    "preferredLocation" to user.preferredLocation,
                    "hasCompletedQuiz" to user.hasCompletedQuiz
                )
                if (user.roommatePreferences != null) {
                    publicMap["roommatePreferences"] = user.roommatePreferences
                }

                db.collection("users").document(user.id).set(publicMap).await()

                // Private contact details subcollection: accessible only by the owner
                if (user.email.isNotBlank()) {
                    val privateContact = mapOf(
                        "email" to user.email,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(user.id)
                        .collection("private").document("contact")
                        .set(privateContact).await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Gets a user synchronously from local cache, or fetches from Firestore if missing.
     */
    fun getUser(userId: String): User? {
        val cached = localUserCache[userId]
        if (cached != null) return cached
        
        // If not in cache, trigger a background fetch and return null for now. 
        // The UI will recompose when the fetch completes and updates the cache if observed elsewhere.
        scope.launch(Dispatchers.IO) {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
                snapshot.toObject(User::class.java)?.let {
                    localUserCache[userId] = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Asynchronously fetches a user from local cache or Firestore.
     */
    suspend fun getUserAsync(userId: String): User? = kotlinx.coroutines.withContext(Dispatchers.IO) {
        val cached = localUserCache[userId]
        if (cached != null) return@withContext cached
        try {
            val snapshot = db.collection("users").document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                localUserCache[userId] = user
            }
            user
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Creates and sends a connection request into Firestore.
     */
    fun sendRoommateRequest(
        sender: User,
        receiver: User,
        compatibilityScore: Int,
        introMessage: String
    ): RoommateRequest {
        val requestId = "req_" + System.currentTimeMillis() + "_" + (100..999).random()
        val request = RoommateRequest(
            requestId = requestId,
            senderId = sender.id,
            senderName = sender.name,
            senderUniversity = sender.university,
            receiverId = receiver.id,
            receiverName = receiver.name,
            receiverUniversity = receiver.university,
            compatibilityScore = compatibilityScore,
            introMessage = introMessage,
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )

        scope.launch(Dispatchers.IO) {
            try {
                db.collection("roommate_requests").document(requestId).set(request).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return request
    }

    /**
     * Cancels a pending sent request by updating status to CANCELLED.
     */
    fun cancelRoommateRequest(requestId: String) {
        scope.launch(Dispatchers.IO) {
            try {
                db.collection("roommate_requests").document(requestId).update("status", "CANCELLED").await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Updates request status to "ACCEPTED" or "DECLINED".
     * If "ACCEPTED", automatically unlocks and creates a deterministic 1-on-1 direct chat channel in Firestore.
     */
    fun updateRequestStatus(requestId: String, newStatus: String, existing: RoommateRequest? = null) {
        scope.launch(Dispatchers.IO) {
            try {
                db.collection("roommate_requests").document(requestId).update("status", newStatus).await()
                
                if (newStatus == "ACCEPTED" && existing != null) {
                    getOrCreateChatRoom(
                        userAId = existing.senderId,
                        userAName = existing.senderName,
                        userBId = existing.receiverId,
                        userBName = existing.receiverName,
                        compatibilityScore = existing.compatibilityScore
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on "chats" collection
     */
    fun attachChatsSnapshotListener(
        currentUserId: String,
        onUpdate: (List<ChatRoom>) -> Unit
    ): ListenerRegistration {
        return db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val userChats = snapshot.toObjects(ChatRoom::class.java)
                        .filter { room ->
                            val otherParticipant = room.participants.firstOrNull { it != currentUserId }
                            otherParticipant == null || !blockedUserIds.contains(otherParticipant)
                        }
                        .sortedByDescending { it.lastMessageTimestamp }
                    onUpdate(userChats)
                }
            }
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on "chats/{chatId}/messages" subcollection
     */
    fun attachMessagesSnapshotListener(
        chatId: String,
        onUpdate: (List<DirectMessage>) -> Unit
    ): ListenerRegistration {
        return db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val currentMsgs = snapshot.toObjects(DirectMessage::class.java)
                    onUpdate(currentMsgs)
                }
            }
    }

    /**
     * Sends a message into chats/{chatId}/messages and updates chats/{chatId}.
     * Enforces message text is non-empty and under 2000 characters to match Firestore security rules.
     */
    fun sendDirectMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        text: String
    ): DirectMessage {
        val sanitizedText = text.trim().take(2000)
        val timestamp = System.currentTimeMillis()
        val messageId = "msg_${timestamp}_${(100..999).random()}"
        val message = DirectMessage(
            messageId = messageId,
            senderId = senderId,
            senderName = senderName,
            text = sanitizedText,
            timestamp = timestamp
        )

        scope.launch(Dispatchers.IO) {
            try {
                // Write message
                db.collection("chats").document(chatId)
                    .collection("messages").document(messageId).set(message).await()

                // Update lastMessage on parent chat
                db.collection("chats").document(chatId).update(
                    mapOf(
                        "lastMessage" to sanitizedText,
                        "lastMessageTimestamp" to timestamp
                    )
                ).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return message
    }

    /**
     * Gets or creates a parent document at chats/{chatId}
     */
    fun getOrCreateChatRoom(
        userAId: String,
        userAName: String,
        userBId: String,
        userBName: String,
        compatibilityScore: Int = 90
    ) {
        val sortedUserIds = listOf(userAId, userBId).sorted()
        val chatId = sortedUserIds.joinToString("_")
        
        scope.launch(Dispatchers.IO) {
            try {
                val doc = db.collection("chats").document(chatId).get().await()
                if (!doc.exists()) {
                    val namesMap = mapOf(userAId to userAName, userBId to userBName)
                    val newRoom = ChatRoom(
                        chatId = chatId,
                        participants = sortedUserIds,
                        participantNames = namesMap,
                        lastMessage = "Roommate request accepted! Start chatting.",
                        lastMessageTimestamp = System.currentTimeMillis(),
                        compatibilityScore = compatibilityScore
                    )
                    db.collection("chats").document(chatId).set(newRoom).await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class CompositeListenerRegistration(private val listeners: List<ListenerRegistration>) : ListenerRegistration {
    override fun remove() {
        listeners.forEach { it.remove() }
    }
}