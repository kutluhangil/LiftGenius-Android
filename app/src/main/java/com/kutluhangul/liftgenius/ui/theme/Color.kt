package com.kutluhangul.liftgenius.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand palette from DESIGN.md — fixed palette, no Material dynamic color.

// ── Brand / accents (DESIGN.md §1) ──
val Accent = Color(0xFFFF6B1A)          // neon orange — primary
val AccentMid = Color(0xFFFF4455)       // ember red — gradient middle
val AccentSecondary = Color(0xFFFF2D8B) // neon pink — secondary

// Text over the neon accents: near-black keeps contrast where white would fail.
val OnAccent = Color(0xFF1F0B00)
val OnAccentSecondary = Color(0xFF25030F)

// Containers (derived tints of the brand colors)
val AccentContainerDark = Color(0xFF3A1B07)
val OnAccentContainerDark = Color(0xFFFFD9C2)
val AccentSecondaryContainerDark = Color(0xFF3C0E24)
val OnAccentSecondaryContainerDark = Color(0xFFFFD3E6)
val AccentContainerLight = Color(0xFFFFE3D1)
val OnAccentContainerLight = Color(0xFF54220A)
val AccentSecondaryContainerLight = Color(0xFFFFD9E9)
val OnAccentSecondaryContainerLight = Color(0xFF570A2E)

// ── Status (§2) ──
val Success = Color(0xFF34D399)
val Warning = Color(0xFFFF9F0A)
val Danger = Color(0xFFFF4455)

// ── Backgrounds (§3) ──
val BgPrimaryDark = Color(0xFF0F0E13)   // charcoal
val BgSecondaryDark = Color(0xFF18171E)
val BgTertiaryDark = Color(0xFF22212A)
val BgPrimaryLight = Color(0xFFF8F6FA)  // warm white
val BgSecondaryLight = Color(0xFFFFFFFF)
val BgTertiaryLight = Color(0xFFEAE7EE)

// ── Text (§4) ──
val TextPrimaryDark = Color(0xFFFCFAFF)
val TextSecondaryDark = TextPrimaryDark.copy(alpha = 0.68f)
val TextTertiaryDark = TextPrimaryDark.copy(alpha = 0.35f)
val TextPrimaryLight = Color(0xFF100C18)
val TextSecondaryLight = TextPrimaryLight.copy(alpha = 0.68f)
val TextTertiaryLight = TextPrimaryLight.copy(alpha = 0.35f)

// ── Glass & borders (§5) ──
val CardBorderDark = Accent.copy(alpha = 0.14f)
val CardBorderLight = AccentSecondary.copy(alpha = 0.10f)
val GlassHairlineDark = Color.White.copy(alpha = 0.12f)
val GlassHairlineLight = Color.White.copy(alpha = 0.90f)
val GlassSpecularDark = Color.White.copy(alpha = 0.18f)
val GlassSpecularLight = Color.White.copy(alpha = 0.95f)
val LedgerDividerDark = Color.White.copy(alpha = 0.08f)
val LedgerDividerLight = Color(0xFF1A1420).copy(alpha = 0.09f)

// ── Brand gradient (§1) — kept as Brush; gradients never enter the M3 color scheme ──
val BrandGradient = Brush.linearGradient(listOf(Accent, AccentMid, AccentSecondary))
val BrandGradientVertical = Brush.verticalGradient(listOf(Accent, AccentMid, AccentSecondary))
