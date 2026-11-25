package com.example.syncmate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.syncmate.data.local.AppPreferences
import com.example.syncmate.data.local.room.SyncMateDatabase
import com.example.syncmate.data.remote.RetrofitClient
import com.example.syncmate.data.repository.FirebaseAuthRepositoryImpl
import com.example.syncmate.data.repository.DevAuthRepositoryImpl
import com.example.syncmate.data.repository.NetworkContractRepositoryImpl
import com.example.syncmate.data.repository.NetworkFakeListingRepositoryImpl
import com.example.syncmate.data.repository.NetworkHousingRepositoryImpl
import com.example.syncmate.data.repository.NetworkPredictionRepositoryImpl
import com.example.syncmate.data.repository.NetworkChatRepositoryImpl
import com.example.syncmate.ui.components.AmbientLiveBackground
import com.example.syncmate.ui.screens.chat.ChatListScreen
import com.example.syncmate.ui.screens.chat.ChatScreen
import com.example.syncmate.ui.screens.chat.DirectChatScreen
import com.example.syncmate.ui.screens.comparelistings.CompareListingsScreen
import com.example.syncmate.ui.screens.contractguard.ContractGuardScreen
import com.example.syncmate.ui.screens.contractsummary.ContractSummaryScreen
import com.example.syncmate.ui.screens.createaccount.CreateAccountScreen
import com.example.syncmate.ui.screens.dashboard.DashboardScreen
import com.example.syncmate.ui.screens.editprofile.EditProfileScreen
import com.example.syncmate.ui.screens.fairrent.FairRentScreen
import com.example.syncmate.ui.screens.fakelisting.FakeListingScreen
import com.example.syncmate.ui.screens.feedback.FeedbackScreen
import com.example.syncmate.ui.screens.filters.FiltersScreen
import com.example.syncmate.ui.screens.forgotpassword.ForgotPasswordScreen
import com.example.syncmate.ui.screens.helpcenter.HelpCenterScreen
import com.example.syncmate.ui.screens.home.HomeScreen
import com.example.syncmate.ui.screens.leasechecklist.LeaseChecklistScreen
import com.example.syncmate.ui.screens.listingdetails.ListingDetailsScreen
import com.example.syncmate.ui.screens.loading.LoadingScreen
import com.example.syncmate.ui.screens.login.LoginScreen
import com.example.syncmate.ui.screens.notifications.NotificationsScreen
import com.example.syncmate.ui.screens.onboarding.OnboardingQuizScreen
import com.example.syncmate.ui.screens.paymentsafety.PaymentSafetyScreen
import com.example.syncmate.ui.screens.preferences.PreferencesScreen
import com.example.syncmate.ui.screens.privacy.PrivacyScreen
import com.example.syncmate.ui.screens.profile.ProfileScreen
import com.example.syncmate.ui.screens.reportlisting.ReportListingScreen
import com.example.syncmate.ui.screens.roommates.IncomingRequestsScreen
import com.example.syncmate.ui.screens.roommates.RoommateProfileScreen
import com.example.syncmate.ui.screens.roommates.RoommateRequestsScreen
import com.example.syncmate.ui.screens.roommates.RoommatesScreen
import com.example.syncmate.ui.screens.savedlistings.SavedListingsScreen
import com.example.syncmate.ui.screens.search.SearchScreen
import com.example.syncmate.ui.screens.settings.SettingsScreen
import com.example.syncmate.ui.screens.uploadcontract.UploadContractScreen
import com.example.syncmate.ui.screens.uploadphotos.UploadPhotosScreen
import com.example.syncmate.ui.screens.utilitypredictor.UtilityPredictorScreen
import com.example.syncmate.ui.screens.verifyemail.VerifyEmailScreen
import com.example.syncmate.ui.screens.welcome.WelcomeScreen
import com.example.syncmate.ui.theme.SyncmateTheme
import com.example.syncmate.viewmodel.AuthViewModel
import com.example.syncmate.viewmodel.ChatListViewModel
import com.example.syncmate.viewmodel.ChatViewModel
import com.example.syncmate.viewmodel.ContractViewModel
import com.example.syncmate.viewmodel.DashboardViewModel
import com.example.syncmate.viewmodel.DirectChatViewModel
import com.example.syncmate.viewmodel.FakeListingViewModel
import com.example.syncmate.viewmodel.HomeViewModel
import com.example.syncmate.viewmodel.HousingViewModel
import com.example.syncmate.viewmodel.IncomingRequestsViewModel
import com.example.syncmate.viewmodel.OnboardingQuizViewModel
import com.example.syncmate.viewmodel.PredictionViewModel
import com.example.syncmate.viewmodel.RoommateMatchViewModel
import com.example.syncmate.viewmodel.RoommateRequestsViewModel
import com.example.syncmate.viewmodel.SettingsViewModel
import java.net.URLDecoder

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    // Persistent Repositories and Local Storage
    val appPreferences = remember { AppPreferences(context) }
    
    // Initialize Room Database
    val database = remember { SyncMateDatabase.getDatabase(context) }
    
    val apiService = remember {
        RetrofitClient.getApiService(appPreferences).also {
            com.example.syncmate.data.firestore.SyncMateFirestore.initApiService(it)
        }
    }
    val authRepository = remember { FirebaseAuthRepositoryImpl(apiService, appPreferences) }
    val housingRepository = remember { NetworkHousingRepositoryImpl(apiService, database.housingDao()) }
    val predictionRepository = remember { NetworkPredictionRepositoryImpl(apiService) }
    val fakeListingRepository = remember { NetworkFakeListingRepositoryImpl(apiService) }
    val contractRepository = remember { NetworkContractRepositoryImpl(apiService) }
    val chatRepository = remember { NetworkChatRepositoryImpl(apiService, appPreferences) }

    // Shared ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = SimpleViewModelFactory { AuthViewModel(authRepository) })
    val homeViewModel: HomeViewModel = viewModel(factory = SimpleViewModelFactory { HomeViewModel(housingRepository) })
    val housingViewModel: HousingViewModel = viewModel(factory = SimpleViewModelFactory { HousingViewModel(housingRepository) })
    val predictionViewModel: PredictionViewModel = viewModel(factory = SimpleViewModelFactory { PredictionViewModel(predictionRepository) })
    val fakeListingViewModel: FakeListingViewModel = viewModel(factory = SimpleViewModelFactory { FakeListingViewModel(fakeListingRepository) })
    val contractViewModel: ContractViewModel = viewModel(factory = SimpleViewModelFactory { ContractViewModel(contractRepository) })
    val chatViewModel: ChatViewModel = viewModel(factory = SimpleViewModelFactory { ChatViewModel(chatRepository) })
    val settingsViewModel: SettingsViewModel = viewModel(factory = SimpleViewModelFactory { SettingsViewModel(appPreferences) })
    val dashboardViewModel: DashboardViewModel = viewModel(factory = SimpleViewModelFactory { DashboardViewModel(authRepository, housingRepository) })
    val onboardingQuizViewModel: OnboardingQuizViewModel = viewModel(factory = SimpleViewModelFactory { OnboardingQuizViewModel(authRepository) })
    val roommateMatchViewModel: RoommateMatchViewModel = viewModel(factory = SimpleViewModelFactory { RoommateMatchViewModel(authRepository, apiService, appPreferences) })
    val incomingRequestsViewModel: IncomingRequestsViewModel = viewModel(factory = SimpleViewModelFactory { IncomingRequestsViewModel(authRepository) })
    val roommateRequestsViewModel: RoommateRequestsViewModel = viewModel(factory = SimpleViewModelFactory { RoommateRequestsViewModel(authRepository) })
    val chatListViewModel: ChatListViewModel = viewModel(factory = SimpleViewModelFactory { ChatListViewModel(authRepository) })

    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    SyncmateTheme(
        darkTheme = settingsUiState.darkModeEnabled
    ) {
        AmbientLiveBackground(
            darkTheme = settingsUiState.darkModeEnabled
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Loading.route
            ) {
            // 1. Loading Screen
            composable(Screen.Loading.route) {
                LoadingScreen(
                    onLoadingComplete = {
                        val destination = if (authUiState.isLoggedIn) {
                            if (authUiState.user?.hasCompletedQuiz == true) Screen.Dashboard.route else Screen.OnboardingQuiz.route
                        } else {
                            Screen.Welcome.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Welcome Screen
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToCreateAccount = { navController.navigate(Screen.CreateAccount.route) }
                )
            }

            // 3. Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        val destination = if (authUiState.user?.hasCompletedQuiz == true) Screen.Dashboard.route else Screen.OnboardingQuiz.route
                        navController.navigate(destination) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onNavigateToVerifyEmail = { navController.navigate(Screen.VerifyEmail.route) }
                )
            }

            // 4. Create Account Screen
            composable(Screen.CreateAccount.route) {
                CreateAccountScreen(
                    authViewModel = authViewModel,
                    onAccountCreated = {
                        navController.navigate(Screen.OnboardingQuiz.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 5. Onboarding Quiz Screen
            composable(Screen.OnboardingQuiz.route) {
                OnboardingQuizScreen(
                    quizViewModel = onboardingQuizViewModel,
                    onQuizCompleted = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.OnboardingQuiz.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 6. Dashboard Screen
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    dashboardViewModel = dashboardViewModel,
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToFilters = { navController.navigate(Screen.Filters.route) },
                    onNavigateToListingDetails = { id -> navController.navigate(Screen.ListingDetails.createRoute(id)) },
                    onNavigateToFairRent = { navController.navigate(Screen.FairRent.route) },
                    onNavigateToContractGuard = { navController.navigate(Screen.ContractGuard.route) },
                    onNavigateToSavedListings = { navController.navigate(Screen.SavedListings.route) },
                    onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToPreferences = { navController.navigate(Screen.Roommates.route) },
                    onNavigateToSafety = { navController.navigate(Screen.PaymentSafety.route) }
                )
            }

            // Roommates Screen
            composable(Screen.Roommates.route) {
                RoommatesScreen(
                    viewModel = roommateMatchViewModel,
                    onNavigateToProfile = { userId -> navController.navigate(Screen.RoommateProfile.createRoute(userId)) },
                    onNavigateToRequests = { navController.navigate(Screen.RoommateRequests.route) },
                    onNavigateToChat = { chatId, otherUserId, otherUserName ->
                        navController.navigate(Screen.DirectChat.createRoute(chatId, otherUserId, otherUserName))
                    },
                    onNavigateToQuiz = { navController.navigate(Screen.OnboardingQuiz.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Dedicated Incoming Roommate Requests Screen
            composable(Screen.IncomingRequests.route) {
                IncomingRequestsScreen(
                    viewModel = incomingRequestsViewModel,
                    onNavigateToProfile = { userId -> navController.navigate(Screen.RoommateProfile.createRoute(userId)) },
                    onNavigateToChat = { chatId, otherUserId, otherUserName ->
                        navController.navigate(Screen.DirectChat.createRoute(chatId, otherUserId, otherUserName))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Roommate Mutual Profile Screen
            composable(
                route = Screen.RoommateProfile.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                RoommateProfileScreen(
                    userId = userId,
                    viewModel = roommateMatchViewModel,
                    onNavigateToChat = { chatId, otherUserId, otherUserName ->
                        navController.navigate(Screen.DirectChat.createRoute(chatId, otherUserId, otherUserName))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Roommate Connection Requests Screen with BOTH Incoming and Sent Tabs
            composable(Screen.RoommateRequests.route) {
                RoommateRequestsScreen(
                    viewModel = roommateRequestsViewModel,
                    onNavigateToProfile = { userId -> navController.navigate(Screen.RoommateProfile.createRoute(userId)) },
                    onOpenDirectChat = { chatId, otherUserId, otherUserName ->
                        navController.navigate(Screen.DirectChat.createRoute(chatId, otherUserId, otherUserName))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 6. Forgot Password Screen
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 7. Verify Email Screen
            composable(Screen.VerifyEmail.route) {
                VerifyEmailScreen(
                    authViewModel = authViewModel,
                    onVerificationSuccess = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 8. Home Marketplace Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToFilters = { navController.navigate(Screen.Filters.route) },
                    onNavigateToListingDetails = { id -> navController.navigate(Screen.ListingDetails.createRoute(id)) },
                    onNavigateToFairRent = { navController.navigate(Screen.FairRent.route) },
                    onNavigateToUtilityPredictor = { navController.navigate(Screen.UtilityPredictor.route) },
                    onNavigateToCompareListings = { navController.navigate(Screen.CompareListings.route) },
                    onNavigateToFakeListing = { navController.navigate(Screen.FakeListing.route) },
                    onNavigateToUploadPhotos = { navController.navigate(Screen.UploadPhotos.route) },
                    onNavigateToContractGuard = { navController.navigate(Screen.ContractGuard.route) },
                    onNavigateToSavedListings = { navController.navigate(Screen.SavedListings.route) },
                    onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToLeaseChecklist = { navController.navigate(Screen.LeaseChecklist.route) },
                    onNavigateToPaymentSafety = { navController.navigate(Screen.PaymentSafety.route) },
                    onNavigateToHelpCenter = { navController.navigate(Screen.HelpCenter.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                    onNavigateToFeedback = { navController.navigate(Screen.Feedback.route) },
                    onNavigateToPreferences = { navController.navigate(Screen.Preferences.route) },
                    onNavigateToRoommates = { navController.navigate(Screen.Roommates.route) }
                )
            }

            // 9. Search Screen
            composable(Screen.Search.route) {
                SearchScreen(
                    housingViewModel = housingViewModel,
                    onNavigateToListingDetails = { id -> navController.navigate(Screen.ListingDetails.createRoute(id)) },
                    onNavigateToFilters = { navController.navigate(Screen.Filters.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 10. Filters Screen
            composable(Screen.Filters.route) {
                FiltersScreen(
                    housingViewModel = housingViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 11. Listing Details Screen
            composable(
                route = Screen.ListingDetails.route,
                arguments = listOf(navArgument("listingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: "lst_1"
                ListingDetailsScreen(
                    listingId = listingId,
                    housingViewModel = housingViewModel,
                    onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                    onNavigateToReport = { id -> navController.navigate(Screen.ReportListing.createRoute(id)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 12. Fair Rent Screen
            composable(Screen.FairRent.route) {
                FairRentScreen(
                    predictionViewModel = predictionViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 13. Utility Predictor Screen
            composable(Screen.UtilityPredictor.route) {
                UtilityPredictorScreen(
                    predictionViewModel = predictionViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 14. Compare Listings Screen
            composable(Screen.CompareListings.route) {
                CompareListingsScreen(
                    housingViewModel = housingViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 15. Fake Listing Shield Screen
            composable(Screen.FakeListing.route) {
                FakeListingScreen(
                    fakeListingViewModel = fakeListingViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 16. Upload Photos Screen
            composable(Screen.UploadPhotos.route) {
                UploadPhotosScreen(
                    housingViewModel = housingViewModel,
                    onListingCreated = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 17. Report Listing Screen
            composable(
                route = Screen.ReportListing.route,
                arguments = listOf(navArgument("listingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: "lst_1"
                ReportListingScreen(
                    listingId = listingId,
                    housingViewModel = housingViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 18. Contract Guard Screen
            composable(Screen.ContractGuard.route) {
                ContractGuardScreen(
                    contractViewModel = contractViewModel,
                    onNavigateToUploadContract = { navController.navigate(Screen.UploadContract.route) },
                    onNavigateToContractSummary = { navController.navigate(Screen.ContractSummary.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 19. Upload Contract Screen
            composable(Screen.UploadContract.route) {
                UploadContractScreen(
                    contractViewModel = contractViewModel,
                    onContractAnalyzed = {
                        navController.navigate(Screen.ContractSummary.route) {
                            popUpTo(Screen.UploadContract.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 20. Contract Summary Screen
            composable(Screen.ContractSummary.route) {
                ContractSummaryScreen(
                    contractViewModel = contractViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 21. Saved Listings Screen
            composable(Screen.SavedListings.route) {
                SavedListingsScreen(
                    housingViewModel = housingViewModel,
                    onNavigateToListingDetails = { id -> navController.navigate(Screen.ListingDetails.createRoute(id)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 22. Connected Chats Inbox Screen
            composable(Screen.Chat.route) {
                ChatListScreen(
                    viewModel = chatListViewModel,
                    onOpenDirectChat = { chatId, otherUserId, otherUserName ->
                        navController.navigate(Screen.DirectChat.createRoute(chatId, otherUserId, otherUserName))
                    },
                    onOpenAiAssistant = { navController.navigate("ai_chat") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // AI Assistant Chat Screen
            composable("ai_chat") {
                ChatScreen(
                    chatViewModel = chatViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Real-Time 1-on-1 Direct Chat Screen
            composable(
                route = Screen.DirectChat.route,
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("otherUserId") { type = NavType.StringType },
                    navArgument("otherUserName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
                val rawName = backStackEntry.arguments?.getString("otherUserName") ?: "Roommate"
                val otherUserName = try {
                    URLDecoder.decode(rawName, "UTF-8")
                } catch (e: Exception) {
                    rawName
                }

                val directChatViewModel: DirectChatViewModel = viewModel(
                    key = chatId,
                    factory = SimpleViewModelFactory {
                        DirectChatViewModel(chatId, otherUserId, otherUserName, authRepository)
                    }
                )

                DirectChatScreen(
                    viewModel = directChatViewModel,
                    onNavigateToProfile = { uid ->
                        navController.navigate(Screen.RoommateProfile.createRoute(uid))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 23. Notifications Screen
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 24. Profile Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onNavigateToPreferences = { navController.navigate(Screen.Preferences.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onSignOut = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 25. Edit Profile Screen
            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 26. Preferences Screen
            composable(Screen.Preferences.route) {
                PreferencesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 27. Lease Checklist Screen
            composable(Screen.LeaseChecklist.route) {
                LeaseChecklistScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 28. Payment Safety Screen
            composable(Screen.PaymentSafety.route) {
                PaymentSafetyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 29. Help Center Screen
            composable(Screen.HelpCenter.route) {
                HelpCenterScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 30. Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    onNavigateToHelpCenter = { navController.navigate(Screen.HelpCenter.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                    onNavigateToFeedback = { navController.navigate(Screen.Feedback.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onDeleteAccount = {
                        authViewModel.deleteAccount(
                            onSuccess = {
                                navController.navigate(Screen.Welcome.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onError = {}
                        )
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 31. Privacy Screen
            composable(Screen.Privacy.route) {
                PrivacyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 32. Feedback Screen
            composable(Screen.Feedback.route) {
                FeedbackScreen(
                    settingsViewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        }
    }
}

class SimpleViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return creator() as VM
    }
}
