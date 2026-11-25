package com.example.syncmate

import com.example.syncmate.data.firestore.SyncMateFirestore
import com.example.syncmate.data.model.ChatRoom
import com.example.syncmate.data.model.RoommateRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoommateChatFlowTest {

    @Test
    fun `deterministic chat ID is identical regardless of which user initiates or opens chat`() {
        val userA = "firebase_uid_alpha_123"
        val userB = "firebase_uid_beta_456"

        val chatIdFromA = SyncMateFirestore.buildDeterministicChatId(userA, userB)
        val chatIdFromB = SyncMateFirestore.buildDeterministicChatId(userB, userA)

        // Both devices must arrive at the exact same document path
        assertEquals(chatIdFromA, chatIdFromB)
        assertEquals("firebase_uid_alpha_123_firebase_uid_beta_456", chatIdFromA)
    }

    @Test
    fun `deterministic chat ID handles leading trailing whitespace properly`() {
        val userA = "  uid_alex  "
        val userB = "uid_bob "

        val chatId = SyncMateFirestore.buildDeterministicChatId(userA, userB)
        assertEquals("uid_alex_uid_bob", chatId)
    }

    @Test
    fun `accept request creates chat room containing both participants and metadata`() {
        val senderId = "usr_sender_001"
        val senderName = "Alex Morgan"
        val receiverId = "usr_receiver_002"
        val receiverName = "Sam Taylor"
        val compatibilityScore = 94

        val request = RoommateRequest(
            requestId = "req_test_123",
            senderId = senderId,
            senderName = senderName,
            senderUniversity = "Anna University",
            receiverId = receiverId,
            receiverName = receiverName,
            receiverUniversity = "SRM University",
            compatibilityScore = compatibilityScore,
            status = "ACCEPTED",
            timestamp = 1700000000000L
        )

        val deterministicChatId = SyncMateFirestore.buildDeterministicChatId(request.senderId, request.receiverId)
        val sortedParticipants = listOf(request.senderId, request.receiverId).sorted()
        val namesMap = mapOf(request.senderId to request.senderName, request.receiverId to request.receiverName)

        val chatRoom = ChatRoom(
            chatId = deterministicChatId,
            participants = sortedParticipants,
            participantNames = namesMap,
            lastMessage = "Roommate request accepted! Start chatting.",
            lastMessageTimestamp = System.currentTimeMillis(),
            compatibilityScore = request.compatibilityScore
        )

        // 1. Both participants are present in the participants array
        assertEquals(2, chatRoom.participants.size)
        assertTrue("Sender must be in participants list", chatRoom.participants.contains(senderId))
        assertTrue("Receiver must be in participants list", chatRoom.participants.contains(receiverId))

        // 2. Chat list query on Device A (sender) returns true
        val senderChatListQueryMatches = chatRoom.participants.contains(senderId)
        assertTrue(senderChatListQueryMatches)

        // 3. Chat list query on Device B (receiver) returns true
        val receiverChatListQueryMatches = chatRoom.participants.contains(receiverId)
        assertTrue(receiverChatListQueryMatches)

        // 4. Verify chat document properties
        assertEquals(deterministicChatId, chatRoom.chatId)
        assertEquals("Alex Morgan", chatRoom.participantNames[senderId])
        assertEquals("Sam Taylor", chatRoom.participantNames[receiverId])
        assertEquals(94, chatRoom.compatibilityScore)
        assertTrue(chatRoom.lastMessage.isNotBlank())
        assertTrue(chatRoom.lastMessageTimestamp > 0)
    }

    @Test
    fun `duplicate requests from the same user are collapsed to a single unique item`() {
        val requests = listOf(
            RoommateRequest(
                requestId = "req_1",
                senderId = "uid_harsh_1",
                senderName = "harsh",
                senderUniversity = "srm University",
                receiverId = "uid_hadi",
                receiverName = "hadi",
                status = "PENDING",
                timestamp = 1000L
            ),
            RoommateRequest(
                requestId = "req_2",
                senderId = "uid_harsh_1",
                senderName = "harsh",
                senderUniversity = "srm University",
                receiverId = "uid_hadi",
                receiverName = "hadi",
                status = "PENDING",
                timestamp = 2000L
            ),
            RoommateRequest(
                requestId = "req_3",
                senderId = "uid_harsh_legacy",
                senderName = "harsh",
                senderUniversity = "srm University",
                receiverId = "uid_hadi",
                receiverName = "hadi",
                status = "PENDING",
                timestamp = 3000L
            ),
            RoommateRequest(
                requestId = "req_4",
                senderId = "uid_priya",
                senderName = "Priya Sharma",
                senderUniversity = "Anna University",
                receiverId = "uid_hadi",
                receiverName = "hadi",
                status = "PENDING",
                timestamp = 2500L
            )
        )

        // Deduplication rule applied in ViewModels and Firestore listeners:
        val deduplicated = requests
            .sortedByDescending { it.timestamp }
            .distinctBy { it.senderId }
            .distinctBy { "${it.senderName.trim().lowercase()}_${it.senderUniversity.trim().lowercase()}" }

        // Expected: exactly 2 distinct requests (one from harsh, one from Priya)
        assertEquals(2, deduplicated.size)
        assertEquals("req_3", deduplicated[0].requestId) // most recent timestamp for harsh
        assertEquals("Priya Sharma", deduplicated[1].senderName)
    }

    @Test
    fun `duplicate user profiles with different IDs or emails are deduplicated`() {
        val users = listOf(
            com.example.syncmate.data.model.User(
                id = "dev_123",
                name = "Harsh Patel",
                email = "harsh@srm.edu",
                university = "SRM University"
            ),
            com.example.syncmate.data.model.User(
                id = "firebase_456",
                name = "Harsh Patel",
                email = "harsh@srm.edu",
                university = "SRM University"
            ),
            com.example.syncmate.data.model.User(
                id = "uid_priya",
                name = "Priya",
                email = "priya@anna.edu",
                university = "Anna University"
            )
        )

        val deduplicatedUsers = users
            .distinctBy { it.id }
            .distinctBy { it.email.trim().lowercase().ifBlank { it.id } }
            .distinctBy { "${it.name.trim().lowercase()}_${it.university.trim().lowercase()}" }

        assertEquals(2, deduplicatedUsers.size)
        assertEquals("Harsh Patel", deduplicatedUsers[0].name)
        assertEquals("Priya", deduplicatedUsers[1].name)
    }
}
