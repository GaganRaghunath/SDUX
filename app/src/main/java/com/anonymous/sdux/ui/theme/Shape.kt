package com.anonymous.sdux.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

/**
 * Sdux shape tokens — mirrors the radius scale in the JSX theme:
 *   sm=8, md=14, lg=20, xl=28, pill=999
 *
 * Usage: SduxTheme.shapes.md on Card, Surface, etc.
 */
@Immutable
data class SduxShapes(
    val sm:   RoundedCornerShape = RoundedCornerShape(8.dp),
    val md:   RoundedCornerShape = RoundedCornerShape(14.dp),
    val lg:   RoundedCornerShape = RoundedCornerShape(20.dp),
    val xl:   RoundedCornerShape = RoundedCornerShape(28.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(999.dp),
)

internal val SduxDefaultShapes = SduxShapes()
