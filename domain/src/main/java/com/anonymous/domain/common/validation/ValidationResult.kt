package com.anonymous.domain.common.validation

sealed class ValidationResult {

    data object Valid : ValidationResult()

    data class Invalid(val errors: List<String>) : ValidationResult() {
        constructor(vararg errors: String) : this(errors.toList())
    }

    val isValid: Boolean get() = this is Valid
    val errors: List<String> get() = if (this is Invalid) errors else emptyList()
}

operator fun ValidationResult.plus(other: ValidationResult): ValidationResult = when {
    this is ValidationResult.Valid && other is ValidationResult.Valid -> ValidationResult.Valid
    else -> ValidationResult.Invalid(this.errors + other.errors)
}
