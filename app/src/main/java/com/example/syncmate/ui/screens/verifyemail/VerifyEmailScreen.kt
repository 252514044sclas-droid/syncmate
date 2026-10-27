package com.example.syncmate.ui.screens.verifyemail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Icon
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
import com.example.syncmate.ui.components.ErrorMessage
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.AuthViewModel

@Composable
fun VerifyEmailScreen(
    authViewModel: AuthViewModel,
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf(uiState.user?.email ?: "alex.morgan@university.edu") }
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        AppHeader(
            title = "Verify Student Email",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Student Verification",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Enter the 6-digit verification code sent to your .edu email address.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            if (uiState.emailVerificationSuccess) {
                Text(
                    text = "✓ Student Email Verified Successfully!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (uiState.error != null) {
                ErrorMessage(
                    message = uiState.error!!,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Student Email",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.MarkEmailRead, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = code,
                onValueChange = { if (it.length <= 6) code = it },
                label = "6-Digit Verification Code",
                placeholder = "123456",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Pin, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Verify Code",
                isLoading = uiState.isLoading,
                onClick = {
                    authViewModel.verifyEmail(email, code)
                    if (code.length == 6) {
                        onVerificationSuccess()
                    }
                }
            )
        }
    }
}
