package com.example.syncmate.ui.screens.roommates

import androidx.compose.runtime.Composable
import com.example.syncmate.viewmodel.RoommateMatchViewModel

/**
 * First-class Roommate Match Screen entry point.
 */
@Composable
fun RoommateMatchScreen(
    viewModel: RoommateMatchViewModel,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToChat: (chatId: String, otherUserId: String, otherUserName: String) -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateBack: () -> Unit
) {
    RoommatesScreen(
        viewModel = viewModel,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToRequests = onNavigateToRequests,
        onNavigateToChat = onNavigateToChat,
        onNavigateToQuiz = onNavigateToQuiz,
        onNavigateBack = onNavigateBack
    )
}
