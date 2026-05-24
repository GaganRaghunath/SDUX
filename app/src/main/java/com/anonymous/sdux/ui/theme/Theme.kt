package com.anonymous.sdux.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── CompositionLocals ───────────────────────────────────────────────────────

internal val LocalSduxColors     = compositionLocalOf { sduxColorScheme() }
internal val LocalSduxShapes     = compositionLocalOf { SduxDefaultShapes }
internal val LocalSduxTypography = compositionLocalOf { SduxDefaultTypography }

// ─── Theme entry point ───────────────────────────────────────────────────────

/**
 * Root theme wrapper for the SDUX / Sdux design system.
 *
 * Provides [SduxTheme.colors], [SduxTheme.typography], [SduxTheme.shapes],
 * and [SduxTheme.spacing] to all descendant composables, and seeds the
 * underlying [MaterialTheme] with matching M3 color and typography roles.
 *
 * @param palette    Brand palette to activate. Defaults to [SduxPalette.Coral].
 * @param darkTheme  Whether to use dark surface tokens. Follows system by default.
 * @param typography Custom type scale. Defaults to [SduxDefaultTypography].
 */
@Composable
fun SDUXTheme(
    palette: SduxPalette = SduxPalette.Coral,
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: SduxTypography = SduxDefaultTypography,
    content: @Composable () -> Unit,
) {
    val sduxColors     = sduxColorScheme(palette, darkTheme)
    val sduxShapes     = SduxDefaultShapes

    CompositionLocalProvider(
        LocalSduxColors     provides sduxColors,
        LocalSduxTypography provides typography,
        LocalSduxShapes     provides sduxShapes,
    ) {
        MaterialTheme(
            colorScheme = sduxColors.toMaterialColorScheme(),
            typography  = buildM3Typography(typography),
            shapes      = sduxShapes.toMaterialShapes(),
            content     = content,
        )
    }
}

// ─── M3 ColorScheme bridge ───────────────────────────────────────────────────

private fun SduxColorScheme.toMaterialColorScheme() = if (isDark) {
    darkColorScheme(
        primary                = primary,
        onPrimary              = SduxWhite,
        primaryContainer       = primarySoft.copy(alpha = 0.28f),
        onPrimaryContainer     = primarySoft,
        secondary              = accent,
        onSecondary            = SduxWhite,
        secondaryContainer     = accent.copy(alpha = 0.22f),
        onSecondaryContainer   = accentSoft,
        tertiary               = gold,
        onTertiary             = SduxGoldOnLight,
        tertiaryContainer      = gold.copy(alpha = 0.20f),
        onTertiaryContainer    = SduxGoldOnDark,
        background             = bg,
        onBackground           = ink,
        surface                = surface,
        onSurface              = ink,
        surfaceVariant         = surfaceHigh,
        onSurfaceVariant       = ink2,
        surfaceTint            = primary,
        surfaceBright          = surfaceHigh,
        surfaceDim             = bg,
        surfaceContainerLowest = Dark_SurfaceLow,
        surfaceContainerLow    = bg,
        surfaceContainer       = surface,
        surfaceContainerHigh   = surfaceHigh,
        surfaceContainerHighest = surfaceHigh,
        inverseSurface         = Light_Surface,
        inverseOnSurface       = Light_Ink,
        inversePrimary         = primaryDeep,
        outline                = divider.copy(alpha = 0.5f),
        outlineVariant         = divider,
        scrim                  = scrim,
        error                  = SduxErrorDark,
        onError                = Color(0xFF690005),
        errorContainer         = Color(0xFF93000A),
        onErrorContainer       = Color(0xFFFFDAD6),
    )
} else {
    lightColorScheme(
        primary                = primary,
        onPrimary              = SduxWhite,
        primaryContainer       = primarySoft,
        onPrimaryContainer     = primaryDeep,
        secondary              = accent,
        onSecondary            = SduxWhite,
        secondaryContainer     = accentSoft,
        onSecondaryContainer   = accent,
        tertiary               = gold,
        onTertiary             = SduxGoldOnLight,
        tertiaryContainer      = Color(0xFFFFF3CD),
        onTertiaryContainer    = SduxGoldOnLight,
        background             = bg,
        onBackground           = ink,
        surface                = surface,
        onSurface              = ink,
        surfaceVariant         = surfaceLow,
        onSurfaceVariant       = ink2,
        surfaceTint            = primary,
        surfaceBright          = surfaceHigh,
        surfaceDim             = surfaceLow,
        surfaceContainerLowest = bg,
        surfaceContainerLow    = surfaceLow,
        surfaceContainer       = surface,
        surfaceContainerHigh   = surfaceHigh,
        surfaceContainerHighest = surfaceHigh,
        inverseSurface         = Dark_Surface,
        inverseOnSurface       = Dark_Ink,
        inversePrimary         = primarySoft,
        outline                = divider.copy(alpha = 0.5f),
        outlineVariant         = divider,
        scrim                  = scrim,
        error                  = SduxErrorLight,
        onError                = SduxWhite,
        errorContainer         = SduxErrorContainer,
        onErrorContainer       = SduxOnErrorContainer,
    )
}

// ─── M3 Shapes bridge ────────────────────────────────────────────────────────

private fun SduxShapes.toMaterialShapes() = Shapes(
    extraSmall = sm,
    small      = sm,
    medium     = md,
    large      = lg,
    extraLarge = xl,
)
