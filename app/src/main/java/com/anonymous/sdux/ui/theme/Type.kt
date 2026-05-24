package com.anonymous.sdux.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.anonymous.sdux.R

// ─── Font families ───────────────────────────────────────────────────────────
// Place the following files in app/src/main/res/font/ before building:
//   bricolage_grotesque_{regular,medium,semibold,bold,extrabold}.ttf
//   plus_jakarta_sans_{regular,medium,semibold,bold}.ttf
//   jetbrains_mono_{regular,medium,semibold}.ttf

/** Display typeface — expressive, high-impact headings. */
val BricolageGrotesque: FontFamily = FontFamily(
    Font(R.font.bricolage_grotesque_regular,   FontWeight.Normal),
    Font(R.font.bricolage_grotesque_medium,    FontWeight.Medium),
    Font(R.font.bricolage_grotesque_semibold,  FontWeight.SemiBold),
    Font(R.font.bricolage_grotesque_bold,      FontWeight.Bold),
    Font(R.font.bricolage_grotesque_extrabold, FontWeight.ExtraBold),
)

/** UI typeface — clean, readable body and label copy. */
val PlusJakartaSans: FontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_regular,  FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium,   FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold,     FontWeight.Bold),
)

/** Mono typeface — code snippets, badges, live indicators. */
val JetBrainsMono: FontFamily = FontFamily(
    Font(R.font.jetbrains_mono_regular,  FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium,   FontWeight.Medium),
    Font(R.font.jetbrains_mono_semibold, FontWeight.SemiBold),
)

// ─── Sdux type scale ─────────────────────────────────────────────────────────

/**
 * Semantic text styles for the Sdux design language.
 *
 * Three families map to three roles:
 *   [BricolageGrotesque]  → display / heading  (large, expressive)
 *   [PlusJakartaSans]     → title / body / label (UI copy)
 *   [JetBrainsMono]       → mono (badges, code)
 *
 * Note: [labelXs] is Sdux-only and has no M3 counterpart — access it via
 * [SduxTheme.typography] only, not [MaterialTheme.typography].
 *
 * Usage: SduxTheme.typography.headingLg
 */
@Immutable
data class SduxTypography(
    // Display — BricolageGrotesque, hero-level text
    val displayLg: TextStyle,
    val displayMd: TextStyle,
    val displaySm: TextStyle,
    // Heading — BricolageGrotesque, page / section titles
    val headingLg: TextStyle,
    val headingMd: TextStyle,
    val headingSm: TextStyle,
    // Title — PlusJakartaSans, component-level labels
    val titleLg: TextStyle,
    val titleMd: TextStyle,
    val titleSm: TextStyle,
    // Body — PlusJakartaSans, prose / content
    val bodyLg: TextStyle,
    val bodyMd: TextStyle,
    val bodySm: TextStyle,
    // Label — PlusJakartaSans, compact utility copy
    val labelLg: TextStyle,
    val labelMd: TextStyle,
    val labelSm: TextStyle,
    val labelXs: TextStyle,
    // Mono — JetBrainsMono
    val monoMd: TextStyle,
    val monoSm: TextStyle,
)

internal val SduxDefaultTypography = SduxTypography(
    // ── Display ──────────────────────────────────────────────────────────────
    displayLg = TextStyle(
        fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-1.5).sp,
    ),
    displayMd = TextStyle(
        fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 36.sp, letterSpacing = (-1.0).sp,
    ),
    displaySm = TextStyle(
        fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 28.sp, letterSpacing = (-0.5).sp,
    ),
    // ── Heading ──────────────────────────────────────────────────────────────
    headingLg = TextStyle(
        fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.4).sp,
    ),
    headingMd = TextStyle(
        fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.2).sp,
    ),
    headingSm = TextStyle(
        fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = (-0.2).sp,
    ),
    // ── Title ─────────────────────────────────────────────────────────────────
    titleLg = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold,
        fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp,
    ),
    titleMd = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp,
    ),
    titleSm = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.1.sp,
    ),
    // ── Body ──────────────────────────────────────────────────────────────────
    bodyLg = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp,
    ),
    bodyMd = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    bodySm = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.1.sp,
    ),
    // ── Label ─────────────────────────────────────────────────────────────────
    labelLg = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 17.sp, letterSpacing = 0.1.sp,
    ),
    labelMd = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
    ),
    labelSm = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold,
        fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.5.sp,
    ),
    labelXs = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 1.0.sp,
    ),
    // ── Mono ──────────────────────────────────────────────────────────────────
    monoMd = TextStyle(
        fontFamily = JetBrainsMono, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.sp,
    ),
    monoSm = TextStyle(
        fontFamily = JetBrainsMono, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.sp,
    ),
)

// ─── M3 Typography bridge ────────────────────────────────────────────────────

/** Maps Sdux type roles onto M3 slots so Material components render correctly. */
internal fun buildM3Typography(t: SduxTypography) = Typography(
    displayLarge   = t.displayLg,
    displayMedium  = t.displayMd,
    displaySmall   = t.displaySm,
    headlineLarge  = t.headingLg,
    headlineMedium = t.headingMd,
    headlineSmall  = t.headingSm,
    titleLarge     = t.titleLg,
    titleMedium    = t.titleMd,
    titleSmall     = t.titleSm,
    bodyLarge      = t.bodyLg,
    bodyMedium     = t.bodyMd,
    bodySmall      = t.bodySm,
    labelLarge     = t.labelLg,
    labelMedium    = t.labelMd,
    labelSmall     = t.labelSm,
)
