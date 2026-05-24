package com.anonymous.domain.base.validation

import com.anonymous.domain.common.validation.ValidationResult
import com.anonymous.domain.common.validation.plus

abstract class Validator<in Input> {

    abstract fun validate(input: Input): ValidationResult

    infix fun and(other: Validator<@UnsafeVariance Input>): Validator<Input> =
        object : Validator<Input>() {
            override fun validate(input: Input): ValidationResult =
                this@Validator.validate(input) + other.validate(input)
        }
}
