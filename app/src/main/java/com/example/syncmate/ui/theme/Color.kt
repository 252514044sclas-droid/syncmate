package com.example.syncmate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =====================================================================
// SyncMate Dual Adaptive Theme System (Black & Warm Beige / Crisp White)
// =====================================================================

// --- Dark Mode Palette (Pitch Black & Warm Beige) ---
val DarkBackground = Color(0xFF000000)        // Pure Pitch Black
val DarkSurface = Color(0xFF111111)           // Elevated Charcoal-Black
val DarkSurfaceVariant = Color(0xFF181818)    // Subtle Dark Slate-Charcoal
val DarkOutline = Color(0x3DE5D5C0)           // Warm Beige Hairline (~24%)
val DarkOutlineVariant = Color(0x2EE5D5C0)    // Warm Beige Hairline (18%)

val DarkPrimary = Color(0xFFE5D5C0)           // Warm Sand Beige
val DarkOnPrimary = Color(0xFF0A0A0A)         // Deep Black for maximum contrast
val DarkPrimaryContainer = Color(0xFF24201A)  // Deep Warm Charcoal
val DarkOnPrimaryContainer = Color(0xFFE5D5C0)

val DarkSecondary = Color(0xFFF2E8DC)         // Soft Champagne Beige
val DarkOnSecondary = Color(0xFF0A0A0A)
val DarkSecondaryContainer = Color(0xFF2C2721)
val DarkOnSecondaryContainer = Color(0xFFF2E8DC)

val DarkTertiary = Color(0xFFB8A895)          // Muted Taupe
val DarkOnTertiary = Color(0xFF0A0A0A)

val DarkOnBackground = Color(0xFFFAF7F2)      // Warm Off-White (Headings)
val DarkOnSurface = Color(0xFFFAF7F2)         // Warm Off-White
val DarkOnSurfaceVariant = Color(0xFFA89F94)  // Muted Warm Beige-Grey (Subtitles)

// --- Light Mode Palette (Crisp Pure White & Warm Beige / Espresso) ---
val LightBackground = Color(0xFFFFFFFF)       // Crisp Pure White
val LightSurface = Color(0xFFFAF7F2)          // Soft Alabaster / Warm Off-White
val LightSurfaceVariant = Color(0xFFF3ECE1)   // Soft Warm Cream
val LightOutline = Color(0xFFB8A895)          // Warm Taupe Border
val LightOutlineVariant = Color(0xFFD6C5B0)   // Subtle Warm Taupe-Beige Hairline

val LightPrimary = Color(0xFFA68A68)          // Rich Warm Beige / Sand-Bronze (High Contrast)
val LightOnPrimary = Color(0xFFFFFFFF)        // Pure White text/icons
val LightPrimaryContainer = Color(0xFFF5EFE6) // Soft Cream Beige Container
val LightOnPrimaryContainer = Color(0xFF382E26)

val LightSecondary = Color(0xFFEDE2D2)        // Soft Cream Beige
val LightOnSecondary = Color(0xFF141210)       // Deep Charcoal/Black
val LightSecondaryContainer = Color(0xFFE8DCCB)
val LightOnSecondaryContainer = Color(0xFF382E26)

val LightTertiary = Color(0xFF5C4F42)         // Deep Espresso-Taupe
val LightOnTertiary = Color(0xFFFFFFFF)

val LightOnBackground = Color(0xFF121212)     // Deep Charcoal-Black (Headings)
val LightOnSurface = Color(0xFF121212)        // Deep Charcoal-Black
val LightOnSurfaceVariant = Color(0xFF5E574F) // Warm Slate-Brown (Subtitles)

// --- Universal Semantic Accents ---
val RoseError = Color(0xFFEF4444)             // Alert / Critical Risk
val AmberWarning = Color(0xFFF59E0B)          // Warning / Medium Risk
val EmeraldSuccess = Color(0xFF10B981)        // Success / Safe Indicator

// --- Material 3 Dark Color Scheme (Pitch Black & Warm Beige) ---
val DarkColorSchemeColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = RoseError,
    onError = Color.White
)

// --- Material 3 Light Color Scheme (Crisp White & Warm Beige) ---
val LightColorSchemeColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = RoseError,
    onError = Color.White
)

// =====================================================================
// Adaptive Dynamic Composable Getters for Complete App Compatibility
// =====================================================================
val SlateBackground: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val SlateCard: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val SlateCardBorder: Color
    @Composable
    get() = MaterialTheme.colorScheme.outlineVariant

val TextPrimary: Color
    @Composable
    get() = MaterialTheme.colorScheme.onBackground

val TextSecondary: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val IndigoPrimary: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

val IndigoVariant: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondary

val EmeraldGreenPrimary: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary
