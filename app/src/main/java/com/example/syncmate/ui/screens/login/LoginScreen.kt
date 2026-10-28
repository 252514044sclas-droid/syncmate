package com.example.syncmate.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.ErrorMessage
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToVerifyEmail: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("alex.morgan@university.edu") }
    var password by remember { mutableStateOf("student123") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Sign In",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Sign in with your student email to access sublets and AI tools.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )

                if (uiState.error != null) {
                    ErrorMessage(
                        message = uiState.error!!,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Student Email (.edu)",
                    placeholder = "student@university.edu",
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = TextSecondary)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.rememberDevice,
                            onCheckedChange = { authViewModel.toggleRememberDevice(it) },
                            colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary)
                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToForgotPassword() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column {
                PrimaryButton(
                    text = "Sign In",
                    isLoading = uiState.isLoading,
                    onClick = {
                        authViewModel.login(email, password)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecondaryButton(
                    text = "Verify Email Code",
                    onClick = onNavigateToVerifyEmail
                )
            }
        }
    }
}
