package com.example.syncmate.ui.screens.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.AuthViewModel

@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(uiState.user?.name ?: "Alex Morgan") }
    var university by remember { mutableStateOf(uiState.user?.university ?: "National Institute of Technology") }
    var bio by remember { mutableStateOf(uiState.user?.bio ?: "Computer Science student looking for summer sublet.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Edit Profile",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Update Student Info",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Update your verified student profile information visible to other subletters.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = university,
                onValueChange = { university = it },
                label = "University / College"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Student Bio",
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Save Profile",
                onClick = { onNavigateBack() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
