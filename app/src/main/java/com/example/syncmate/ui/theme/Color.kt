package com.example.syncmate.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// SyncMate Green Palette - Primary Accent
val EmeraldGreenPrimary = Color(0xFF059669)
val EmeraldGreenVariant = Color(0xFF047857)
val EmeraldGreenLight = Color(0xFF34D399)

// SyncMate Dark Emerald Palette
val SlateBackgroundDark = Color(0xFF0F172A)
val SlateCardDark = Color(0xFF1E293B)
val SlateBorderDark = Color(0xFF047857)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFFA7F3D0)

// SyncMate Light Green & White Palette
val LightBackground = Color(0xFFF8FAFC)
val LightCard = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFA7F3D0)
val TextPrimaryLight = Color(0xFF064E3B)
val TextSecondaryLight = Color(0xFF047857)

// Accent Colors
val IndigoPrimary = EmeraldGreenPrimary
val IndigoVariant = EmeraldGreenVariant
val IndigoLight = EmeraldGreenLight

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

// Material 3 Dark Color Scheme (Emerald & Dark Slate)
val DarkColorSchemeColors = darkColorScheme(
    primary = EmeraldGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldGreenVariant,
    onPrimaryContainer = Color.White,
    secondary = EmeraldGreenLight,
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

// Material 3 Light Color Scheme (Green & Pure White)
val LightColorSchemeColors = lightColorScheme(
    primary = EmeraldGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldGreenLight,
    onPrimaryContainer = Color.Black,
    secondary = EmeraldGreenVariant,
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
