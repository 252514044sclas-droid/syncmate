package com.example.syncmate.ui.screens.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary

@Composable
fun PreferencesScreen(
    onNavigateBack: () -> Unit
) {
    var maxBudget by remember { mutableStateOf("1200") }
    var locationPref by remember { mutableStateOf("Campus Town") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        AppHeader(
            title = "Housing Preferences",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Match Preferences",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppTextField(
                value = maxBudget,
                onValueChange = { maxBudget = it },
                label = "Target Monthly Budget ($)"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = locationPref,
                onValueChange = { locationPref = it },
                label = "Preferred Neighborhood"
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Save Preferences",
                onClick = { onNavigateBack() }
            )
        }
    }
}
