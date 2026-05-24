package com.anonymous.sdux.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Single access point for all Sdux design tokens inside a [SDUXTheme] scope.
 *
 * Mirrors the pattern of [androidx.compose.material3.MaterialTheme]:
 *
 * ```kotlin
 * Text(
 *     text  = "Good morning",
 *     style = SduxTheme.typography.headingLg,
 *     color = SduxTheme.colors.ink,
 * )
 * Box(
 *     modifier = Modifier
 *         .background(SduxTheme.colors.primarySoft, SduxTheme.shapes.lg)
 *         .padding(SduxTheme.spacing.x5)
 * )
 * ```
 *
 * Note: [SduxTypography.labelXs] is Sdux-only and has no MaterialTheme counterpart.
 * Always access it via [SduxTheme.typography], never via [androidx.compose.material3.MaterialTheme.typography].
 */
object SduxTheme {

    /** Resolved color tokens for the active palette + mode. */
    val colors: SduxColorScheme
        @Composable @ReadOnlyComposable
        get() = LocalSduxColors.current

    /** Semantic text styles (display, heading, title, body, label, mono). */
    val typography: SduxTypography
        @Composable @ReadOnlyComposable
        get() = LocalSduxTypography.current

    /** Corner-radius tokens (sm=8, md=14, lg=20, xl=28, pill=999). */
    val shapes: SduxShapes
        @Composable @ReadOnlyComposable
        get() = LocalSduxShapes.current

    /** 4 dp × n spacing scale. Stateless — safe to use anywhere. */
    val spacing: SduxSpacing get() = SduxSpacing
}
