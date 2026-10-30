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
import com.example.syncmate.data.remote.RetrofitClient
import com.example.syncmate.data.repository.DevAuthRepositoryImpl
import com.example.syncmate.data.repository.MockContractRepositoryImpl
import com.example.syncmate.data.repository.MockFakeListingRepositoryImpl
import com.example.syncmate.data.repository.MockHousingRepositoryImpl
import com.example.syncmate.data.repository.MockPredictionRepositoryImpl
import com.example.syncmate.ui.screens.chat.ChatScreen
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
import com.example.syncmate.viewmodel.ContractViewModel
import com.example.syncmate.viewmodel.DashboardViewModel
import com.example.syncmate.viewmodel.FakeListingViewModel
import com.example.syncmate.viewmodel.HomeViewModel
import com.example.syncmate.viewmodel.HousingViewModel
import com.example.syncmate.viewmodel.OnboardingQuizViewModel
import com.example.syncmate.viewmodel.PredictionViewModel
import com.example.syncmate.viewmodel.SettingsViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    // Persistent Repositories and Local Storage
    val appPreferences = remember { AppPreferences(context) }
    val apiService = remember { RetrofitClient.getApiService(appPreferences) }
    val authRepository = remember { DevAuthRepositoryImpl(apiService, appPreferences) }
    val housingRepository = remember { MockHousingRepositoryImpl(apiService) }
    val predictionRepository = remember { MockPredictionRepositoryImpl(apiService) }
    val fakeListingRepository = remember { MockFakeListingRepositoryImpl(apiService) }
    val contractRepository = remember { MockContractRepositoryImpl(apiService) }

    // Shared ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = SimpleViewModelFactory { AuthViewModel(authRepository) })
    val homeViewModel: HomeViewModel = viewModel(factory = SimpleViewModelFactory { HomeViewModel(housingRepository) })
    val housingViewModel: HousingViewModel = viewModel(factory = SimpleViewModelFactory { HousingViewModel(housingRepository) })
    val predictionViewModel: PredictionViewModel = viewModel(factory = SimpleViewModelFactory { PredictionViewModel(predictionRepository) })
    val fakeListingViewModel: FakeListingViewModel = viewModel(factory = SimpleViewModelFactory { FakeListingViewModel(fakeListingRepository) })
    val contractViewModel: ContractViewModel = viewModel(factory = SimpleViewModelFactory { ContractViewModel(contractRepository) })
    val settingsViewModel: SettingsViewModel = viewModel(factory = SimpleViewModelFactory { SettingsViewModel(appPreferences) })
    val dashboardViewModel: DashboardViewModel = viewModel(factory = SimpleViewModelFactory { DashboardViewModel(authRepository, housingRepository) })
    val onboardingQuizViewModel: OnboardingQuizViewModel = viewModel(factory = SimpleViewModelFactory { OnboardingQuizViewModel(authRepository) })

    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    SyncmateTheme(
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
                    onNavigateToPreferences = { navController.navigate(Screen.Preferences.route) },
                    onNavigateToSafety = { navController.navigate(Screen.PaymentSafety.route) }
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
                    onNavigateToPreferences = { navController.navigate(Screen.Preferences.route) }
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

            // 22. Chat Screen
            composable(Screen.Chat.route) {
                ChatScreen(
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

class SimpleViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return creator() as VM
    }
}
