package com.example.syncmate.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val user = uiState.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Student Profile",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user?.name ?: "Alex Morgan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = user?.email ?: "alex.morgan@university.edu",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            StatBadge(text = "VERIFIED STUDENT (.EDU)", backgroundColor = EmeraldSuccess)

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateCard)
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "University: ${user?.university ?: "National Institute of Technology"}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Preferred Lease: ${user?.preferredLeaseTerm ?: "Summer 2025"}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Max Budget: ${CurrencyFormatter.formatMonthlyRent(user?.maxBudget ?: 15000.0)}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Preferred Area: ${user?.preferredLocation ?: "Adyar / IIT Campus"}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Edit Student Profile",
                onClick = onNavigateToEditProfile
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Housing & Roommate Preferences",
                onClick = onNavigateToPreferences
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Sign Out",
                onClick = {
                    authViewModel.signOut()
                    onSignOut()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
