package com.anonymous.sdux.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Sdux spacing scale — n × 4 dp, matching the JSX `space(n)` token.
 *
 * Named aliases cover the most-used steps; for arbitrary values use [invoke]:
 *   SduxSpacing(3)  →  12.dp
 *   SduxSpacing.x5  →  20.dp
 */
object SduxSpacing {
    /** Returns [n] × 4.dp. */
    operator fun invoke(n: Int): Dp = (n * 4).dp

    val x1  =  4.dp
    val x2  =  8.dp
    val x3  = 12.dp
    val x4  = 16.dp
    val x5  = 20.dp
    val x6  = 24.dp
    val x7  = 28.dp
    val x8  = 32.dp
    val x10 = 40.dp
    val x12 = 48.dp
    val x14 = 56.dp
    val x16 = 64.dp
}
