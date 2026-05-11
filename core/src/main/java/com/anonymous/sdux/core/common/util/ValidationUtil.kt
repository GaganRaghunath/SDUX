package com.anonymous.sdux.core.common.util

object ValidationUtil {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = emailRegex.matches(email)

    fun isValidPassword(password: String): Boolean = password.length >= 8

    fun isNotEmpty(value: String): Boolean = value.isNotBlank()
}
