package com.example.syncmate.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// SubletSync Dark Design System Palette
val SlateBackground = Color(0xFF0F172A)
val SlateCard = Color(0xFF1E293B)
val SlateCardBorder = Color(0xFF334155)
val SlateInput = Color(0xFF1E293B)

val IndigoPrimary = Color(0xFF6366F1)
val IndigoVariant = Color(0xFF4F46E5)
val IndigoLight = Color(0xFF818CF8)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val EmeraldSuccess = Color(0xFF10B981)
val AmberWarning = Color(0xFFF59E0B)
val RoseError = Color(0xFFEF4444)
val CyanInfo = Color(0xFF06B6D4)

// Material 3 Dark Colors
val DarkColorSchemeColors = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoVariant,
    onPrimaryContainer = Color.White,
    secondary = IndigoLight,
    onSecondary = Color.Black,
    background = SlateBackground,
    onBackground = TextPrimary,
    surface = SlateCard,
    onSurface = TextPrimary,
    surfaceVariant = SlateCardBorder,
    onSurfaceVariant = TextSecondary,
    error = RoseError,
    onError = Color.White
)
