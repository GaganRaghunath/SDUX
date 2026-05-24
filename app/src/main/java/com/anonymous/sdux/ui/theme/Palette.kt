package com.anonymous.sdux.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

enum class SduxPalette(val displayName: String) {
    Coral("Coral & Ink"),
    Plum("Plum & Sage"),
    Marine("Marine & Clay"),
    Forest("Forest & Amber"),
}

/**
 * Sdux design token surface — the resolved color set for a given palette + mode.
 *
 * Usage: access via [SduxTheme.colors] inside any composable wrapped by [SDUXTheme].
 *
 * Brand layer (palette-specific, mode-invariant):
 *   primary / primaryDeep / primarySoft — three-step primary ramp
 *   accent / accentSoft                 — secondary brand color + tint
 *   success / gold                      — semantic fixed colors
 *
 * Surface layer (mode-specific):
 *   bg → surfaceLow → surface → surfaceHigh (ascending elevation in light; reversed dark)
 *   divider / scrim                     — overlay colors
 *
 * Foreground layer:
 *   ink > ink2 > ink3                   — primary → secondary → tertiary text
 */
@Immutable
data class SduxColorScheme(
    // ── Brand ─────────────────────────────────────────────────────────────────
    val primary: Color,
    val primaryDeep: Color,
    val primarySoft: Color,
    val accent: Color,
    val accentSoft: Color,
    val success: Color,
    val gold: Color,
    // ── Surfaces ──────────────────────────────────────────────────────────────
    val bg: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val surfaceLow: Color,
    val divider: Color,
    // ── Foreground ────────────────────────────────────────────────────────────
    val ink: Color,
    val ink2: Color,
    val ink3: Color,
    val scrim: Color,
    // ── Meta ──────────────────────────────────────────────────────────────────
    val palette: SduxPalette,
    val isDark: Boolean,
)

@Stable
fun sduxColorScheme(
    palette: SduxPalette = SduxPalette.Coral,
    dark: Boolean = false,
): SduxColorScheme {
    val brand = brandFor(palette)
    val surf  = if (dark) darkSurface else lightSurface
    return SduxColorScheme(
        primary     = brand.primary,
        primaryDeep = brand.primaryDeep,
        primarySoft = brand.primarySoft,
        accent      = brand.accent,
        accentSoft  = brand.accentSoft,
        success     = brand.success,
        gold        = brand.gold,
        bg          = surf.bg,
        surface     = surf.surface,
        surfaceHigh = surf.surfaceHigh,
        surfaceLow  = surf.surfaceLow,
        divider     = surf.divider,
        ink         = surf.ink,
        ink2        = surf.ink2,
        ink3        = surf.ink3,
        scrim       = surf.scrim,
        palette     = palette,
        isDark      = dark,
    )
}

// ─── Internal token holders ───────────────────────────────────────────────────

private data class BrandTokens(
    val primary: Color, val primaryDeep: Color, val primarySoft: Color,
    val accent: Color, val accentSoft: Color, val success: Color, val gold: Color,
)

private data class SurfaceTokens(
    val bg: Color, val surface: Color, val surfaceHigh: Color, val surfaceLow: Color,
    val divider: Color, val ink: Color, val ink2: Color, val ink3: Color, val scrim: Color,
)

private fun brandFor(p: SduxPalette) = when (p) {
    SduxPalette.Coral  -> coralBrand
    SduxPalette.Plum   -> plumBrand
    SduxPalette.Marine -> marineBrand
    SduxPalette.Forest -> forestBrand
}

private val coralBrand = BrandTokens(
    primary = Coral_Primary, primaryDeep = Coral_PrimaryDeep, primarySoft = Coral_PrimaryLight,
    accent = Coral_Accent, accentSoft = Coral_AccentLight, success = Coral_Success, gold = Coral_Gold,
)
private val plumBrand = BrandTokens(
    primary = Plum_Primary, primaryDeep = Plum_PrimaryDeep, primarySoft = Plum_PrimaryLight,
    accent = Plum_Accent, accentSoft = Plum_AccentLight, success = Plum_Success, gold = Plum_Gold,
)
private val marineBrand = BrandTokens(
    primary = Marine_Primary, primaryDeep = Marine_PrimaryDeep, primarySoft = Marine_PrimaryLight,
    accent = Marine_Accent, accentSoft = Marine_AccentLight, success = Marine_Success, gold = Marine_Gold,
)
private val forestBrand = BrandTokens(
    primary = Forest_Primary, primaryDeep = Forest_PrimaryDeep, primarySoft = Forest_PrimaryLight,
    accent = Forest_Accent, accentSoft = Forest_AccentLight, success = Forest_Success, gold = Forest_Gold,
)

private val lightSurface = SurfaceTokens(
    bg = Light_Bg, surface = Light_Surface, surfaceHigh = Light_SurfaceHigh,
    surfaceLow = Light_SurfaceLow, divider = Light_Divider,
    ink = Light_Ink, ink2 = Light_Ink2, ink3 = Light_Ink3, scrim = Light_Scrim,
)
private val darkSurface = SurfaceTokens(
    bg = Dark_Bg, surface = Dark_Surface, surfaceHigh = Dark_SurfaceHigh,
    surfaceLow = Dark_SurfaceLow, divider = Dark_Divider,
    ink = Dark_Ink, ink2 = Dark_Ink2, ink3 = Dark_Ink3, scrim = Dark_Scrim,
)
