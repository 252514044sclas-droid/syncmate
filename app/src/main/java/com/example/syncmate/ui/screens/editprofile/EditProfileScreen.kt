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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.syncmate.ui.components.ErrorMessage
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
    val user = uiState.user

    var name by remember { mutableStateOf(user?.name ?: "") }
    var university by remember { mutableStateOf(user?.university ?: "") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }

    LaunchedEffect(user) {
        if (user != null) {
            name = user.name
            university = user.university
            bio = user.bio
        }
    }

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

            if (uiState.profileSaveError != null) {
                ErrorMessage(
                    message = uiState.profileSaveError!!,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                placeholder = "Enter your full name"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = university,
                onValueChange = { university = it },
                label = "University / College",
                placeholder = "Enter your college or university"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Student Bio",
                placeholder = "Share a short bio about yourself and your study interests...",
                singleLine = false,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Save Profile",
                isLoading = uiState.isProfileSaving,
                onClick = {
                    authViewModel.updateProfile(
                        name = name,
                        university = university,
                        bio = bio,
                        onSuccess = {
                            onNavigateBack()
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
