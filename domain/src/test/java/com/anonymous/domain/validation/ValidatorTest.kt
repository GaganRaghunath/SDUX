package com.anonymous.domain.validation

import com.anonymous.domain.base.validation.Validator
import com.anonymous.domain.common.validation.ValidationResult
import com.anonymous.testshared.base.BaseUnitTest
import com.anonymous.testshared.factory.StubFactory
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.Test

class ValidatorTest : BaseUnitTest() {

    // ── Concrete validators ────────────────────────────────────────────────────

    private val nonBlank = object : Validator<String>() {
        override fun validate(input: String) =
            if (input.isNotBlank()) ValidationResult.Valid
            else ValidationResult.Invalid("Must not be blank")
    }

    private val minLength = object : Validator<String>() {
        override fun validate(input: String) =
            if (input.length >= 5) ValidationResult.Valid
            else ValidationResult.Invalid("Min 5 characters")
    }

    private val noSpaces = object : Validator<String>() {
        override fun validate(input: String) =
            if (' ' !in input) ValidationResult.Valid
            else ValidationResult.Invalid("No spaces allowed")
    }

    // ── Single validator ──────────────────────────────────────────────────────

    @Test
    fun `nonBlank - valid for non-empty string`() {
        nonBlank.validate(StubFactory.anyString()).isValid shouldBe true
    }

    @Test
    fun `nonBlank - invalid for blank string`() {
        val result = nonBlank.validate("   ")
        result.isValid shouldBe false
        result.errors shouldContain "Must not be blank"
    }

    @Test
    fun `minLength - valid when length meets threshold`() {
        minLength.validate("hello").isValid shouldBe true
    }

    @Test
    fun `minLength - invalid when below threshold`() {
        val result = minLength.validate("hi")
        result.isValid shouldBe false
        result.errors shouldContain "Min 5 characters"
    }

    // ── Combinator (and) ──────────────────────────────────────────────────────

    @Test
    fun `and - returns Valid when all validators pass`() {
        val combined = nonBlank and minLength and noSpaces
        combined.validate("hello").isValid shouldBe true
    }

    @Test
    fun `and - accumulates all error messages when multiple fail`() {
        val combined = nonBlank and minLength
        val result = combined.validate("")
        result.isValid shouldBe false
        result.errors shouldHaveSize 2
        result.errors shouldContain "Must not be blank"
        result.errors shouldContain "Min 5 characters"
    }

    @Test
    fun `and - reports only failing validator's error`() {
        val combined = nonBlank and minLength
        val result = combined.validate("hi")  // non-blank but too short
        result.isValid shouldBe false
        result.errors shouldHaveSize 1
        result.errors shouldContain "Min 5 characters"
    }

    @Test
    fun `and - three-way combination accumulates three errors`() {
        val combined = nonBlank and minLength and noSpaces
        val result = combined.validate("a b")  // fails minLength and noSpaces; is non-blank
        result.isValid shouldBe false
        result.errors shouldContain "Min 5 characters"
        result.errors shouldContain "No spaces allowed"
    }
}
