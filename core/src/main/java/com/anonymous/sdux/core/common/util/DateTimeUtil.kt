package com.anonymous.sdux.core.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtil {
    private const val DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    fun format(timestamp: Long, pattern: String = DEFAULT_FORMAT): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))

    fun parse(dateString: String, pattern: String = DEFAULT_FORMAT): Long =
        SimpleDateFormat(pattern, Locale.getDefault()).parse(dateString)?.time ?: 0L
}
