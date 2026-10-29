package com.example.syncmate.ui.screens.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.OnboardingQuizViewModel

@Composable
fun OnboardingQuizScreen(
    quizViewModel: OnboardingQuizViewModel,
    onQuizCompleted: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()
    val prefs = uiState.preferences
    val haptic = LocalHapticFeedback.current

    val animatedProgress by animateFloatAsState(
        targetValue = (uiState.currentQuestionIndex + 1) / 10f,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "quiz_progress"
    )

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onQuizCompleted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (uiState.currentQuestionIndex > 0) {
                        quizViewModel.previousQuestion()
                    } else {
                        onNavigateBack()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "Question ${uiState.currentQuestionIndex + 1} of 10",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
                Text(
                    text = "Roommate Lifestyle Onboarding",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // Animated Progress Indicator Bar
        LinearProgressIndicator(
            progress = { animatedProgress },
            color = IndigoPrimary,
            trackColor = SlateCardBorder,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(3.dp))
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
                Spacer(modifier = Modifier.height(16.dp))

                when (uiState.currentQuestionIndex) {
                    0 -> QuizQuestionOptionGrid(
                        questionNum = 1,
                        title = "What is your sleep schedule rhythm?",
                        subtitle = "Select your typical daily sleeping pattern.",
                        options = listOf("Early Bird", "Night Owl", "Flexible"),
                        selectedOption = prefs.sleepSchedule,
                        onOptionSelected = {
                            quizViewModel.updateSleepSchedule(it)
                            quizViewModel.nextQuestion()
                        }
                    )

                    1 -> QuizSliderQuestion(
                        questionNum = 2,
                        title = "How clean do you keep your living space?",
                        subtitle = "Rate your cleanliness preference from 1 to 5.",
                        value = prefs.cleanlinessRating,
                        onValueChange = { quizViewModel.updateCleanliness(it) }
                    )

                    2 -> QuizQuestionOptionGrid(
                        questionNum = 3,
                        title = "What is your guest policy preference?",
                        subtitle = "How often are visitors and friends allowed over?",
                        options = listOf("Never", "Weekends Only", "Anytime"),
                        selectedOption = prefs.guestPolicy,
                        onOptionSelected = {
                            quizViewModel.updateGuestPolicy(it)
                            quizViewModel.nextQuestion()
                        }
                    )

                    3 -> QuizQuestionOptionGrid(
                        questionNum = 4,
                        title = "What is your noise level tolerance?",
                        subtitle = "Select your preferred noise level while studying or relaxing.",
                        options = listOf("Quiet Study", "Moderate", "Loud Music OK"),
                        selectedOption = prefs.noiseTolerance,
                        onOptionSelected = {
                            quizViewModel.updateNoiseTolerance(it)
                            quizViewModel.nextQuestion()
                        }
                    )

                    4 -> QuizQuestionOptionGrid(
                        questionNum = 5,
                        title = "Where do you usually study?",
                        subtitle = "Select your primary study location habit.",
                        options = listOf("In Room", "Library", "Common Areas"),
                        selectedOption = prefs.studyHabits,
                        onOptionSelected = {
                            quizViewModel.updateStudyHabits(it)
                            quizViewModel.nextQuestion()
                        }
                    )

                    5 -> QuizQuestionOptionGrid(
                        questionNum = 6,
                        title = "What are your dietary preferences?",
                        subtitle = "Select your meal and kitchen preference.",
                        options = listOf("Vegetarian", "Non-Vegetarian", "Vegan", "Flexible"),
                        selectedOption = prefs.dietaryPreference,
                        onOptionSelected = {
                            quizViewModel.updateDietaryPreference(it)
                            quizViewModel.nextQuestion()
                        }
                    )

                    6 -> QuizQuestionOptionGrid(
                        questionNum = 7,
                        title = "What is your smoking policy?",
                        subtitle = "Are you tolerant of smoking/vaping near living areas?",
                        options = listOf("Non-Smoking Only", "Tolerant"),
                        selectedOption = if (prefs.smokingOption) "Tolerant" else "Non-Smoking Only",
                        onOptionSelected = {
                            quizViewModel.updateSmokingOption(it == "Tolerant")
                            quizViewModel.nextQuestion()
                        }
                    )

                    7 -> QuizQuestionOptionGrid(
                        questionNum = 8,
                        title = "How do you feel about pets?",
                        subtitle = "Select your pet tolerance or ownership status.",
                        options = listOf("Love Pets", "No Pets", "Pet Owner"),
                        selectedOption = prefs.petPreference,
                        onOptionSelected = {
                            quizViewModel.updatePetPreference(it)
                            quizViewModel.nextQuestion()
                        }
                    )

                    8 -> QuizQuestionOptionGrid(
                        questionNum = 9,
                        title = "What is your item sharing style?",
                        subtitle = "How do you handle shared essentials and groceries?",
                        options = listOf("Strict Mine-is-Mine", "Open to Sharing Essentials"),
                        selectedOption = prefs.sharingStyle,
                        onOptionSelected = {
                            quizViewModel.updateSharingStyle(it)
                            quizViewModel.nextQuestion()
                        }
                    )

                    9 -> QuizBudgetInputQuestion(
                        questionNum = 10,
                        title = "What is your maximum monthly rent budget?",
                        subtitle = "Enter your target budget in Indian Rupees (₹).",
                        currentBudget = prefs.maxBudgetINR,
                        onBudgetChanged = { quizViewModel.updateMaxBudget(it) }
                    )
                }
            }

            // Bottom Navigation Controls
            Column {
                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.currentQuestionIndex > 0) {
                        SecondaryButton(
                            text = "Back",
                            onClick = { quizViewModel.previousQuestion() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    PrimaryButton(
                        text = if (uiState.currentQuestionIndex == 9) "Finish Setup" else "Next Question",
                        isLoading = uiState.isLoading,
                        onClick = { quizViewModel.nextQuestion() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuizQuestionOptionGrid(
    questionNum: Int,
    title: String,
    subtitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        options.forEach { option ->
            val isSelected = option == selectedOption

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) IndigoPrimary.copy(alpha = 0.2f) else SlateCard)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) IndigoPrimary else SlateCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOptionSelected(option)
                    }
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) IndigoPrimary else TextPrimary
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = IndigoPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizSliderQuestion(
    questionNum: Int,
    title: String,
    subtitle: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    val labels = mapOf(
        1 to "1 - Casual / Relaxed",
        2 to "2 - Average Cleanliness",
        3 to "3 - Neat & Organized",
        4 to "4 - Very Clean & Tidy",
        5 to "5 - Spotless / Pristine"
    )

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SlateCard)
                .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Rating: ${value} / 5",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )

                Text(
                    text = labels[value] ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Slider(
                    value = value.toFloat(),
                    onValueChange = { onValueChange(it.toInt()) },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = IndigoPrimary,
                        activeTrackColor = IndigoPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun QuizBudgetInputQuestion(
    questionNum: Int,
    title: String,
    subtitle: String,
    currentBudget: Double,
    onBudgetChanged: (Double) -> Unit
) {
    var budgetText by remember { mutableStateOf(currentBudget.toInt().toString()) }

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SlateCard)
                .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Selected Budget: ${CurrencyFormatter.formatMonthlyRent(budgetText.toDoubleOrNull() ?: currentBudget)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSuccess,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                AppTextField(
                    value = budgetText,
                    onValueChange = { text ->
                        if (text.length <= 6 && text.all { it.isDigit() }) {
                            budgetText = text
                            val parsed = text.toDoubleOrNull()
                            if (parsed != null) {
                                onBudgetChanged(parsed)
                            }
                        }
                    },
                    label = "Max Monthly Rent in INR (₹)",
                    placeholder = "15000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}
