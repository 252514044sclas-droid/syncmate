package com.example.syncmate.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// SubletSync Dark Palette
val SlateBackgroundDark = Color(0xFF0F172A)
val SlateCardDark = Color(0xFF1E293B)
val SlateBorderDark = Color(0xFF334155)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)

// SubletSync Light Palette
val LightBackground = Color(0xFFF8FAFC)
val LightCard = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFE2E8F0)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)

// Accent Colors
val IndigoPrimary = Color(0xFF6366F1)
val IndigoVariant = Color(0xFF4F46E5)
val IndigoLight = Color(0xFF818CF8)

val TextMuted = Color(0xFF64748B)
val EmeraldSuccess = Color(0xFF10B981)
val AmberWarning = Color(0xFFF59E0B)
val RoseError = Color(0xFFEF4444)
val CyanInfo = Color(0xFF06B6D4)

// Legacy alias definitions for backward compatibility
val SlateBackground = SlateBackgroundDark
val SlateCard = SlateCardDark
val SlateCardBorder = SlateBorderDark
val TextPrimary = TextPrimaryDark
val TextSecondary = TextSecondaryDark

// Material 3 Dark Color Scheme
val DarkColorSchemeColors = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoVariant,
    onPrimaryContainer = Color.White,
    secondary = IndigoLight,
    onSecondary = Color.Black,
    background = SlateBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SlateCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateBorderDark,
    onSurfaceVariant = TextSecondaryDark,
    error = RoseError,
    onError = Color.White
)

// Material 3 Light Color Scheme
val LightColorSchemeColors = lightColorScheme(
    primary = IndigoVariant,
    onPrimary = Color.White,
    primaryContainer = IndigoLight,
    onPrimaryContainer = Color.White,
    secondary = IndigoPrimary,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightBorder,
    onSurfaceVariant = TextSecondaryLight,
    error = RoseError,
    onError = Color.White
)
