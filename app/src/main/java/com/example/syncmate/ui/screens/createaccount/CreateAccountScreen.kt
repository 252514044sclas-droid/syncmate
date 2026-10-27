package com.example.syncmate.ui.screens.createaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun CreateAccountScreen(
    authViewModel: AuthViewModel,
    onAccountCreated: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onAccountCreated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        AppHeader(
            title = "Create Account",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Join SubletSync",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Create your account with your university student email to get verified.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            if (uiState.error != null) {
                ErrorMessage(
                    message = uiState.error!!,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                placeholder = "Alex Morgan",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Student Email (.edu)",
                placeholder = "alex.m@university.edu",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = university,
                onValueChange = { university = it },
                label = "University / College",
                placeholder = "State University",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.School, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "At least 6 characters",
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Create Account",
                isLoading = uiState.isLoading,
                onClick = {
                    authViewModel.signUp(name, email, password, university)
                }
            )
        }
    }
}
