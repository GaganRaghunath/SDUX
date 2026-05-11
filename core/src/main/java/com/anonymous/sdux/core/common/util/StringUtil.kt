package com.anonymous.sdux.core.common.util

object StringUtil {
    fun String.isNotBlankOrEmpty(): Boolean = isNotBlank() && isNotEmpty()

    fun String.truncate(maxLength: Int, suffix: String = "..."): String =
        if (length <= maxLength) this else take(maxLength - suffix.length) + suffix

    fun String.toTitleCase(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}
