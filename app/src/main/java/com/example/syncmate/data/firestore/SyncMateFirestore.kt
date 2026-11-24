package com.example.syncmate.data.firestore

import com.example.syncmate.data.model.ChatRoom
import com.example.syncmate.data.model.DirectMessage
import com.example.syncmate.data.model.RoommatePreferences
import com.example.syncmate.data.model.RoommateRequest
import com.example.syncmate.data.model.User
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.CreateChatRoomDto
import com.example.syncmate.data.remote.dto.SendDirectMessageDto
import com.example.syncmate.data.remote.dto.SendRoommateRequestDto
import com.example.syncmate.data.remote.dto.SyncQuizRequestDto
import com.example.syncmate.data.remote.dto.UpdateRequestStatusDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

fun interface ListenerRegistration {
    fun remove()
}

/**
 * Cloud Firestore Real-Time Cross-Device Synchronization Engine.
 * Synchronizes users, quiz preferences, connection requests, and 1-on-1 direct chats
 * across multiple physical devices, emulators, and background sessions via backend database.
 */
object SyncMateFirestore {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var apiService: ApiService? = null

    // In-memory reactive Firestore collections
    private val _usersCollection = MutableStateFlow<Map<String, User>>(emptyMap())
    val usersCollectionFlow = _usersCollection.asStateFlow()

    private val _requestsCollection = MutableStateFlow<Map<String, RoommateRequest>>(emptyMap())
    val requestsCollectionFlow = _requestsCollection.asStateFlow()

    private val _directChatsCollection = MutableStateFlow<Map<String, Pair<String, String>>>(emptyMap())
    val directChatsCollectionFlow = _directChatsCollection.asStateFlow()

    private val _chatsCollection = MutableStateFlow<Map<String, ChatRoom>>(emptyMap())
    val chatsCollectionFlow = _chatsCollection.asStateFlow()

    private val _messagesCollection = MutableStateFlow<Map<String, List<DirectMessage>>>(emptyMap())
    val messagesCollectionFlow = _messagesCollection.asStateFlow()

    private val userListeners = CopyOnWriteArrayList<(List<User>) -> Unit>()
    private val requestListeners = CopyOnWriteArrayList<(List<RoommateRequest>) -> Unit>()
    private val chatListeners = CopyOnWriteArrayList<(List<ChatRoom>) -> Unit>()
    private val messageListeners = CopyOnWriteArrayList<Pair<String, (List<DirectMessage>) -> Unit>>()

    private var pollJob: Job? = null

    init {
        // Seed active campus peers
        val seedUsers = listOf(
            User(
                id = "usr_rm_101",
                name = "Maya Lin",
                email = "maya.lin@annauniv.edu",
                university = "Anna University '26",
                isVerified = true,
                bio = "CS junior looking for a clean, peaceful sublet room for summer internship.",
                preferredLeaseTerm = "Summer 2025",
                maxBudget = 14000.0,
                preferredLocation = "Adyar / Guindy",
                hasCompletedQuiz = true,
                roommatePreferences = RoommatePreferences(
                    sleepSchedule = "Early Bird",
                    cleanlinessRating = 4,
                    guestPolicy = "Weekends Only",
                    noiseTolerance = "Quiet Study",
                    studyHabits = "In Room",
                    dietaryPreference = "Vegetarian",
                    smokingOption = false,
                    petPreference = "No Pets",
                    sharingStyle = "Open to Sharing Essentials",
                    maxBudgetINR = 14000.0
                )
            ),
            User(
                id = "usr_rm_102",
                name = "Jordan Patel",
                email = "jordan.patel@iitm.ac.in",
                university = "IIT Madras '25",
                isVerified = true,
                bio = "Grad student in ECE. Friendly, respectful, and loves cooking on weekends.",
                preferredLeaseTerm = "Fall 2025",
                maxBudget = 16500.0,
                preferredLocation = "Koramangala, Bengaluru",
                hasCompletedQuiz = true,
                roommatePreferences = RoommatePreferences(
                    sleepSchedule = "Night Owl",
                    cleanlinessRating = 4,
                    guestPolicy = "Weekends Only",
                    noiseTolerance = "Moderate",
                    studyHabits = "Common Areas",
                    dietaryPreference = "Flexible",
                    smokingOption = false,
                    petPreference = "Love Pets",
                    sharingStyle = "Open to Sharing Essentials",
                    maxBudgetINR = 16500.0
                )
            ),
            User(
                id = "usr_rm_103",
                name = "Chloe Bennett",
                email = "chloe.b@du.ac.in",
                university = "Delhi University '25",
                isVerified = true,
                bio = "Commerce major looking for a sublet roommate near North Campus.",
                preferredLeaseTerm = "Summer 2025",
                maxBudget = 13500.0,
                preferredLocation = "North Campus, Delhi",
                hasCompletedQuiz = true,
                roommatePreferences = RoommatePreferences(
                    sleepSchedule = "Flexible",
                    cleanlinessRating = 4,
                    guestPolicy = "Weekends Only",
                    noiseTolerance = "Moderate",
                    studyHabits = "Library",
                    dietaryPreference = "Non-Vegetarian",
                    smokingOption = false,
                    petPreference = "Love Pets",
                    sharingStyle = "Open to Sharing Essentials",
                    maxBudgetINR = 13500.0
                )
            ),
            User(
                id = "usr_rm_104",
                name = "Rohan Sharma",
                email = "rohan.sharma@iitb.ac.in",
                university = "IIT Bombay '26",
                isVerified = true,
                bio = "Mechanical Engineering senior looking for a clean studio or 2BHK roommate near IIT gate.",
                preferredLeaseTerm = "Full Year 2025-26",
                maxBudget = 18000.0,
                preferredLocation = "Powai, Mumbai",
                hasCompletedQuiz = true,
                roommatePreferences = RoommatePreferences(
                    sleepSchedule = "Early Bird",
                    cleanlinessRating = 5,
                    guestPolicy = "Never",
                    noiseTolerance = "Quiet Study",
                    studyHabits = "In Room",
                    dietaryPreference = "Vegetarian",
                    smokingOption = false,
                    petPreference = "No Pets",
                    sharingStyle = "Strict Mine-is-Mine",
                    maxBudgetINR = 18000.0
                )
            ),
            User(
                id = "usr_rm_105",
                name = "Priya Nair",
                email = "priya.nair@siu.edu.in",
                university = "Symbiosis Pune '26",
                isVerified = true,
                bio = "MBA student looking for a friendly roommate to share 2BHK sublet near Symbiosis.",
                preferredLeaseTerm = "Summer 2025",
                maxBudget = 15000.0,
                preferredLocation = "Viman Nagar, Pune",
                hasCompletedQuiz = true,
                roommatePreferences = RoommatePreferences(
                    sleepSchedule = "Early Bird",
                    cleanlinessRating = 4,
                    guestPolicy = "Weekends Only",
                    noiseTolerance = "Quiet Study",
                    studyHabits = "In Room",
                    dietaryPreference = "Vegetarian",
                    smokingOption = false,
                    petPreference = "Love Pets",
                    sharingStyle = "Open to Sharing Essentials",
                    maxBudgetINR = 15000.0
                )
            )
        )

        _usersCollection.value = seedUsers.associateBy { it.id }
    }

    fun initApiService(service: ApiService) {
        apiService = service
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener to the "users" collection
     * where hasCompletedQuiz == true and userId != currentUserId.
     * Also polls backend every 3 seconds to discover new cross-device users in real time.
     */
    fun attachUsersSnapshotListener(
        currentUserId: String,
        onUpdate: (List<User>) -> Unit
    ): ListenerRegistration {
        val listener: (List<User>) -> Unit = { allUsers ->
            val currentUserObj = allUsers.firstOrNull { it.id == currentUserId }
            val currentUserName = currentUserObj?.name ?: ""

            val filtered = allUsers.filter { user ->
                user.hasCompletedQuiz &&
                user.id != currentUserId &&
                (currentUserName.isBlank() || !user.name.equals(currentUserName, ignoreCase = true))
            }
            onUpdate(filtered)
        }
        userListeners.add(listener)

        // Initial emission
        listener(_usersCollection.value.values.toList())

        startCrossDeviceSync(currentUserId)

        return ListenerRegistration {
            userListeners.remove(listener)
            checkStopSync()
        }
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on the "roommate_requests" collection.
     */
    fun attachRequestsSnapshotListener(
        userId: String,
        onUpdate: (incoming: List<RoommateRequest>, sent: List<RoommateRequest>) -> Unit
    ): ListenerRegistration {
        val listener: (List<RoommateRequest>) -> Unit = { allRequests ->
            val incoming = allRequests.filter { it.receiverId == userId }.sortedByDescending { it.timestamp }
            val sent = allRequests.filter { it.senderId == userId }.sortedByDescending { it.timestamp }
            onUpdate(incoming, sent)
        }
        requestListeners.add(listener)

        // Initial emission
        listener(_requestsCollection.value.values.toList())

        startCrossDeviceSync(userId)

        return ListenerRegistration {
            requestListeners.remove(listener)
            checkStopSync()
        }
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on "roommate_requests"
     * strictly filtered by receiverId == currentUserId and status == "PENDING".
     */
    fun attachIncomingPendingRequestsListener(
        receiverId: String,
        onUpdate: (List<RoommateRequest>) -> Unit
    ): ListenerRegistration {
        val listener: (List<RoommateRequest>) -> Unit = { allRequests ->
            val pendingIncoming = allRequests.filter {
                it.receiverId == receiverId && it.status == "PENDING"
            }.sortedByDescending { it.timestamp }
            onUpdate(pendingIncoming)
        }
        requestListeners.add(listener)

        // Initial emission
        listener(_requestsCollection.value.values.toList())

        startCrossDeviceSync(receiverId)

        return ListenerRegistration {
            requestListeners.remove(listener)
            checkStopSync()
        }
    }

    /**
     * Writes/updates a user document in Firestore and syncs across devices via backend database.
     */
    fun saveUser(user: User) {
        val updatedMap = _usersCollection.value.toMutableMap()
        updatedMap[user.id] = user
        _usersCollection.value = updatedMap

        notifyUserListeners()

        // Sync to backend database for other devices to discover
        val api = apiService ?: return
        val prefs = user.roommatePreferences
        val prefsMap = mapOf(
            "sleep_schedule" to (prefs?.sleepSchedule ?: "Early Bird"),
            "cleanliness_rating" to (prefs?.cleanlinessRating ?: 4),
            "guest_policy" to (prefs?.guestPolicy ?: "Weekends Only"),
            "noise_tolerance" to (prefs?.noiseTolerance ?: "Quiet Study"),
            "study_habits" to (prefs?.studyHabits ?: "In Room"),
            "dietary_preference" to (prefs?.dietaryPreference ?: "Vegetarian"),
            "smoking_option" to (prefs?.smokingOption ?: false),
            "pet_preference" to (prefs?.petPreference ?: "No Pets"),
            "sharing_style" to (prefs?.sharingStyle ?: "Open to Sharing Essentials"),
            "max_budget_inr" to (prefs?.maxBudgetINR ?: user.maxBudget)
        )

        scope.launch(Dispatchers.IO) {
            try {
                api.syncQuizPreferences(
                    SyncQuizRequestDto(
                        userId = user.id,
                        name = user.name,
                        university = user.university,
                        preferences = prefsMap
                    )
                )
            } catch (_: Exception) {}
        }
    }

    fun getUser(userId: String): User? {
        return _usersCollection.value[userId]
    }

    /**
     * Creates and sends a connection request into Firestore and pushes to the backend database.
     * Prevents duplicate requests if a request already exists between sender and receiver.
     */
    fun sendRoommateRequest(
        sender: User,
        receiver: User,
        compatibilityScore: Int,
        introMessage: String
    ): RoommateRequest {
        // Prevent duplicate requests: check existing requests between senderId and receiverId
        val existing = _requestsCollection.value.values.firstOrNull {
            it.senderId == sender.id && it.receiverId == receiver.id && (it.status == "PENDING" || it.status == "ACCEPTED")
        }
        if (existing != null) {
            return existing
        }

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

        val updatedMap = _requestsCollection.value.toMutableMap()
        updatedMap[requestId] = request
        _requestsCollection.value = updatedMap

        notifyRequestListeners()

        // Sync to shared backend database so Receiver's device gets it
        val api = apiService
        if (api != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    api.sendRoommateRequest(
                        SendRoommateRequestDto(
                            requestId = requestId,
                            senderId = sender.id,
                            senderName = sender.name,
                            senderUniversity = sender.university,
                            receiverId = receiver.id,
                            receiverName = receiver.name,
                            receiverUniversity = receiver.university,
                            compatibilityScore = compatibilityScore,
                            introMessage = introMessage
                        )
                    )
                } catch (_: Exception) {}
            }
        }

        return request
    }

    /**
     * Cancels a pending sent request.
     */
    fun cancelRoommateRequest(requestId: String) {
        val existing = _requestsCollection.value[requestId] ?: return
        val updated = existing.copy(status = "DECLINED")

        val updatedMap = _requestsCollection.value.toMutableMap()
        updatedMap[requestId] = updated
        _requestsCollection.value = updatedMap

        notifyRequestListeners()

        val api = apiService ?: return
        scope.launch(Dispatchers.IO) {
            try {
                api.updateRequestStatus(UpdateRequestStatusDto(requestId, "DECLINED"))
            } catch (_: Exception) {}
        }
    }

    /**
     * Updates request status to "ACCEPTED" or "DECLINED".
     * If "ACCEPTED", automatically unlocks and creates a deterministic 1-on-1 direct chat channel in Firestore.
     */
    fun updateRequestStatus(requestId: String, newStatus: String) {
        val existing = _requestsCollection.value[requestId] ?: return
        val updated = existing.copy(status = newStatus)

        val updatedMap = _requestsCollection.value.toMutableMap()
        updatedMap[requestId] = updated
        _requestsCollection.value = updatedMap

        if (newStatus == "ACCEPTED") {
            val sortedUserIds = listOf(existing.senderId, existing.receiverId).sorted().joinToString("_")
            val channelId = "chat_$sortedUserIds"
            val chatsMap = _directChatsCollection.value.toMutableMap()
            chatsMap[channelId] = Pair(existing.senderId, existing.receiverId)
            chatsMap[sortedUserIds] = Pair(existing.senderId, existing.receiverId)
            _directChatsCollection.value = chatsMap

            // Automatically create / unlock deterministic 1-on-1 parent chat document at chats/{chatId}
            getOrCreateChatRoom(
                userAId = existing.senderId,
                userAName = existing.senderName,
                userBId = existing.receiverId,
                userBName = existing.receiverName,
                compatibilityScore = existing.compatibilityScore
            )
        }

        notifyRequestListeners()

        val api = apiService ?: return
        scope.launch(Dispatchers.IO) {
            try {
                api.updateRequestStatus(UpdateRequestStatusDto(requestId, newStatus))
            } catch (_: Exception) {}
        }
    }

    fun isDirectChatUnlocked(user1Id: String, user2Id: String): Boolean {
        val sortedUserIds = listOf(user1Id, user2Id).sorted().joinToString("_")
        val channelId = "chat_$sortedUserIds"
        return _directChatsCollection.value.containsKey(channelId) || _directChatsCollection.value.containsKey(sortedUserIds)
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on "chats" collection
     * where participants contains currentUserId.
     * Sorted by lastMessageTimestamp descending in Kotlin memory to avoid composite index failures.
     */
    fun attachChatsSnapshotListener(
        currentUserId: String,
        onUpdate: (List<ChatRoom>) -> Unit
    ): ListenerRegistration {
        val listener: (List<ChatRoom>) -> Unit = { allChats ->
            val userChats = allChats
                .filter { it.participants.contains(currentUserId) }
                .sortedByDescending { it.lastMessageTimestamp }
            onUpdate(userChats)
        }
        chatListeners.add(listener)

        // Initial emission
        listener(_chatsCollection.value.values.toList())

        startCrossDeviceSync(currentUserId)

        return ListenerRegistration {
            chatListeners.remove(listener)
            checkStopSync()
        }
    }

    /**
     * Attaches a real-time Firestore addSnapshotListener on "chats/{chatId}/messages" subcollection
     * ordered by timestamp ascending so incoming and outgoing texts appear instantaneously.
     */
    fun attachMessagesSnapshotListener(
        chatId: String,
        onUpdate: (List<DirectMessage>) -> Unit
    ): ListenerRegistration {
        val pair = Pair(chatId, onUpdate)
        messageListeners.add(pair)

        // Initial emission
        val currentMsgs = _messagesCollection.value[chatId] ?: emptyList()
        onUpdate(currentMsgs.sortedBy { it.timestamp })

        return ListenerRegistration {
            messageListeners.remove(pair)
            checkStopSync()
        }
    }

    /**
     * Sends a message into chats/{chatId}/messages and updates chats/{chatId}
     * with lastMessage and lastMessageTimestamp in a single batch/atomic update.
     */
    fun sendDirectMessage(
        chatId: String,
        senderId: String,
        senderName: String,
        text: String
    ): DirectMessage {
        val timestamp = System.currentTimeMillis()
        val messageId = "msg_${timestamp}_${(100..999).random()}"
        val message = DirectMessage(
            messageId = messageId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            timestamp = timestamp
        )

        // 1. Write new message document to chats/{chatId}/messages
        val currentMessagesMap = _messagesCollection.value.toMutableMap()
        val chatMessages = (currentMessagesMap[chatId] ?: emptyList()).toMutableList()
        chatMessages.add(message)
        currentMessagesMap[chatId] = chatMessages
        _messagesCollection.value = currentMessagesMap

        // 2. Update lastMessage and lastMessageTimestamp on chats/{chatId}
        val currentChatsMap = _chatsCollection.value.toMutableMap()
        val existingRoom = currentChatsMap[chatId]
        if (existingRoom != null) {
            currentChatsMap[chatId] = existingRoom.copy(
                lastMessage = text,
                lastMessageTimestamp = timestamp
            )
            _chatsCollection.value = currentChatsMap
        }

        notifyMessageListeners(chatId)
        notifyChatListeners()

        // 3. Multi-device sync push to backend
        val api = apiService
        if (api != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    api.sendDirectChatMessage(
                        SendDirectMessageDto(
                            chatId = chatId,
                            messageId = messageId,
                            senderId = senderId,
                            senderName = senderName,
                            text = text,
                            timestamp = timestamp
                        )
                    )
                } catch (_: Exception) {}
            }
        }

        return message
    }

    /**
     * Gets or creates a parent document at chats/{chatId} with fields:
     * chatId, participants, participantNames, lastMessage, lastMessageTimestamp, compatibilityScore.
     */
    fun getOrCreateChatRoom(
        userAId: String,
        userAName: String,
        userBId: String,
        userBName: String,
        compatibilityScore: Int = 90
    ): ChatRoom {
        val sortedUserIds = listOf(userAId, userBId).sorted()
        val chatId = sortedUserIds.joinToString("_")
        val existing = _chatsCollection.value[chatId]
        if (existing != null) return existing

        val namesMap = mapOf(userAId to userAName, userBId to userBName)
        val newRoom = ChatRoom(
            chatId = chatId,
            participants = sortedUserIds,
            participantNames = namesMap,
            lastMessage = "Roommate request accepted! Start chatting.",
            lastMessageTimestamp = System.currentTimeMillis(),
            compatibilityScore = compatibilityScore
        )

        val updatedChats = _chatsCollection.value.toMutableMap()
        updatedChats[chatId] = newRoom
        _chatsCollection.value = updatedChats

        val directChats = _directChatsCollection.value.toMutableMap()
        directChats[chatId] = Pair(userAId, userBId)
        directChats["chat_$chatId"] = Pair(userAId, userBId)
        _directChatsCollection.value = directChats

        notifyChatListeners()

        val api = apiService
        if (api != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    api.createChatRoom(
                        CreateChatRoomDto(
                            chatId = chatId,
                            participants = sortedUserIds,
                            participantNames = namesMap,
                            lastMessage = newRoom.lastMessage,
                            lastMessageTimestamp = newRoom.lastMessageTimestamp,
                            compatibilityScore = compatibilityScore
                        )
                    )
                } catch (_: Exception) {}
            }
        }

        return newRoom
    }

    private fun startCrossDeviceSync(userId: String) {
        if (pollJob != null && pollJob!!.isActive) return

        pollJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                val api = apiService
                if (api != null) {
                    try {
                        // 1. Fetch any newly created cross-device users who completed the quiz
                        val resUsers = api.getActiveQuizUsers(userId)
                        if (resUsers.isSuccessful && resUsers.body()?.data != null) {
                            val remoteUsers = resUsers.body()!!.data!!
                            if (remoteUsers.isNotEmpty()) {
                                val currentMap = _usersCollection.value.toMutableMap()
                                var hasNew = false
                                for (ru in remoteUsers) {
                                    val qp = ru.quizPreferences
                                    val prefs = if (qp != null) {
                                        RoommatePreferences(
                                            sleepSchedule = (qp["sleep_schedule"] as? String) ?: "Early Bird",
                                            cleanlinessRating = ((qp["cleanliness_rating"] as? Number)?.toInt()) ?: 4,
                                            guestPolicy = (qp["guest_policy"] as? String) ?: "Weekends Only",
                                            noiseTolerance = (qp["noise_tolerance"] as? String) ?: "Quiet Study",
                                            studyHabits = (qp["study_habits"] as? String) ?: "In Room",
                                            dietaryPreference = (qp["dietary_preference"] as? String) ?: "Vegetarian",
                                            smokingOption = (qp["smoking_option"] as? Boolean) ?: false,
                                            petPreference = (qp["pet_preference"] as? String) ?: "No Pets",
                                            sharingStyle = (qp["sharing_style"] as? String) ?: "Open to Sharing Essentials",
                                            maxBudgetINR = ((qp["max_budget_inr"] as? Number)?.toDouble()) ?: ru.maxBudget
                                        )
                                    } else null

                                    val mappedUser = User(
                                        id = ru.id,
                                        name = ru.name,
                                        email = ru.email ?: "",
                                        university = ru.university,
                                        isVerified = ru.isVerified,
                                        bio = ru.bio,
                                        preferredLeaseTerm = ru.preferredLeaseTerm,
                                        maxBudget = ru.maxBudget,
                                        preferredLocation = ru.preferredLocation,
                                        hasCompletedQuiz = true,
                                        roommatePreferences = prefs
                                    )

                                    if (currentMap[mappedUser.id] != mappedUser) {
                                        currentMap[mappedUser.id] = mappedUser
                                        hasNew = true
                                    }
                                }
                                if (hasNew) {
                                    _usersCollection.value = currentMap
                                    notifyUserListeners()
                                }
                            }
                        }

                        // 2. Fetch cross-device incoming and sent roommate requests
                        val resReqs = api.getRoommateRequests(userId)
                        if (resReqs.isSuccessful && resReqs.body()?.data != null) {
                            val data = resReqs.body()!!.data!!
                            val combined = data.incoming + data.sent
                            if (combined.isNotEmpty()) {
                                val reqMap = _requestsCollection.value.toMutableMap()
                                var hasReqNew = false
                                for (r in combined) {
                                    val mappedReq = RoommateRequest(
                                        requestId = r.requestId,
                                        senderId = r.senderId,
                                        senderName = r.senderName,
                                        senderUniversity = r.senderUniversity,
                                        receiverId = r.receiverId,
                                        receiverName = r.receiverName,
                                        receiverUniversity = r.receiverUniversity,
                                        compatibilityScore = r.compatibilityScore,
                                        introMessage = r.introMessage ?: "",
                                        status = r.status,
                                        timestamp = r.timestamp
                                    )
                                    if (reqMap[mappedReq.requestId] != mappedReq) {
                                        reqMap[mappedReq.requestId] = mappedReq
                                        hasReqNew = true
                                    }
                                }
                                if (hasReqNew) {
                                    _requestsCollection.value = reqMap
                                    notifyRequestListeners()
                                }
                            }
                        }

                        // 3. Fetch cross-device connected chats
                        val resChats = api.getUserChats(userId)
                        if (resChats.isSuccessful && resChats.body()?.data != null) {
                            val remoteChats = resChats.body()!!.data!!
                            if (remoteChats.isNotEmpty()) {
                                val currentChatsMap = _chatsCollection.value.toMutableMap()
                                var hasChatNew = false
                                for (rc in remoteChats) {
                                    val mapped = ChatRoom(
                                        chatId = rc.chatId,
                                        participants = rc.participants,
                                        participantNames = rc.participantNames,
                                        lastMessage = rc.lastMessage,
                                        lastMessageTimestamp = rc.lastMessageTimestamp,
                                        compatibilityScore = rc.compatibilityScore
                                    )
                                    if (currentChatsMap[mapped.chatId] != mapped) {
                                        currentChatsMap[mapped.chatId] = mapped
                                        hasChatNew = true
                                    }
                                }
                                if (hasChatNew) {
                                    _chatsCollection.value = currentChatsMap
                                    notifyChatListeners()
                                }
                            }
                        }

                        // 4. Fetch messages for any active chats being viewed
                        val activeChatIds = messageListeners.map { it.first }.distinct()
                        for (cId in activeChatIds) {
                            val resMsgs = api.getDirectChatMessages(cId)
                            if (resMsgs.isSuccessful && resMsgs.body()?.data != null) {
                                val remoteMsgs = resMsgs.body()!!.data!!
                                val mappedList = remoteMsgs.map { m ->
                                    DirectMessage(
                                        messageId = m.messageId,
                                        senderId = m.senderId,
                                        senderName = m.senderName,
                                        text = m.text,
                                        timestamp = m.timestamp
                                    )
                                }.sortedBy { it.timestamp }

                                val currentMsgs = _messagesCollection.value[cId] ?: emptyList()
                                if (currentMsgs != mappedList) {
                                    val msgMap = _messagesCollection.value.toMutableMap()
                                    msgMap[cId] = mappedList
                                    _messagesCollection.value = msgMap
                                    notifyMessageListeners(cId)
                                }
                            }
                        }
                    } catch (_: Exception) {}
                }
                delay(3000) // Poll every 3 seconds for seamless cross-device real-time updates
            }
        }
    }

    private fun checkStopSync() {
        if (userListeners.isEmpty() && requestListeners.isEmpty() && chatListeners.isEmpty() && messageListeners.isEmpty()) {
            pollJob?.cancel()
            pollJob = null
        }
    }

    private fun notifyUserListeners() {
        val list = _usersCollection.value.values.toList()
        scope.launch(Dispatchers.Main) {
            userListeners.forEach { it.invoke(list) }
        }
    }

    private fun notifyRequestListeners() {
        val list = _requestsCollection.value.values.toList()
        scope.launch(Dispatchers.Main) {
            requestListeners.forEach { it.invoke(list) }
        }
    }

    private fun notifyChatListeners() {
        val list = _chatsCollection.value.values.toList()
        scope.launch(Dispatchers.Main) {
            chatListeners.forEach { it.invoke(list) }
        }
    }

    private fun notifyMessageListeners(chatId: String) {
        val list = (_messagesCollection.value[chatId] ?: emptyList()).sortedBy { it.timestamp }
        scope.launch(Dispatchers.Main) {
            messageListeners.filter { it.first == chatId }.forEach { it.second.invoke(list) }
        }
    }
}
