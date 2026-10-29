package com.example.syncmate.navigation

sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object CreateAccount : Screen("create_account")
    object ForgotPassword : Screen("forgot_password")
    object VerifyEmail : Screen("verify_email")
    object Home : Screen("home")
    object Dashboard : Screen("dashboard")
    object OnboardingQuiz : Screen("onboarding_quiz")
    object Search : Screen("search")
    object Filters : Screen("filters")
    object ListingDetails : Screen("listing_details/{listingId}") {
        fun createRoute(listingId: String) = "listing_details/$listingId"
    }
    object FairRent : Screen("fair_rent")
    object UtilityPredictor : Screen("utility_predictor")
    object CompareListings : Screen("compare_listings")
    object FakeListing : Screen("fake_listing")
    object UploadPhotos : Screen("upload_photos")
    object ReportListing : Screen("report_listing/{listingId}") {
        fun createRoute(listingId: String) = "report_listing/$listingId"
    }
    object ContractGuard : Screen("contract_guard")
    object UploadContract : Screen("upload_contract")
    object ContractSummary : Screen("contract_summary")
    object SavedListings : Screen("saved_listings")
    object Chat : Screen("chat")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Preferences : Screen("preferences")
    object LeaseChecklist : Screen("lease_checklist")
    object PaymentSafety : Screen("payment_safety")
    object HelpCenter : Screen("help_center")
    object Settings : Screen("settings")
    object Privacy : Screen("privacy")
    object Feedback : Screen("feedback")
}
