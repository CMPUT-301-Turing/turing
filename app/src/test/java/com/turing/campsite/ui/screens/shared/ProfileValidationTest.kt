package com.turing.campsite.ui.screens.shared

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for validateProfile() on the edit profile screen (US 01.07.03)
 */
class ProfileValidationTest {

    @Test
    fun validateProfile_allFilledIn_isValid() {
        val errors = validateProfile("Alice", "alice@example.com", "+1 780-555-0100")

        assertTrue(errors.isValid)
    }

    @Test
    fun validateProfile_onlyName_isValid() {
        // email and phone are optional
        val errors = validateProfile("Alice", "", "")

        assertTrue(errors.isValid)
    }

    @Test
    fun validateProfile_blankName_nameError() {
        val errors = validateProfile("   ", "", "")

        assertFalse(errors.isValid)
        assertEquals("Name is required", errors.name)
    }

    @Test
    fun validateProfile_badEmail_emailError() {
        val errors = validateProfile("Alice", "alice.example.com", "")

        assertEquals("Enter a valid email", errors.email)
        assertNull(errors.name)
    }

    @Test
    fun validateProfile_emailWithSpaces_isTrimmed() {
        val errors = validateProfile("Alice", "  alice@example.com  ", "")

        assertNull(errors.email)
    }

    @Test
    fun validateProfile_phoneWithLetters_phoneError() {
        val errors = validateProfile("Alice", "", "call me")

        assertEquals("Enter a valid phone number", errors.phone)
    }

    @Test
    fun validateProfile_phoneTooShort_phoneError() {
        val errors = validateProfile("Alice", "", "123")

        assertEquals("Enter a valid phone number", errors.phone)
    }
}
