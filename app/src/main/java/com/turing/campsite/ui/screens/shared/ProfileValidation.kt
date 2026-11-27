package com.turing.campsite.ui.screens.shared

/**
 * What is wrong with each field on the edit profile screen.
 *
 * Each value is null when that field is fine.
 *
 * @property name the error for the name field
 * @property email the error for the email field
 * @property phone the error for the phone number field
 */
data class ProfileErrors(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null
) {
    /** True when no field has an error. */
    val isValid: Boolean
        get() = name == null && email == null && phone == null
}

private val emailPattern = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
private val phonePattern = Regex("^\\+?[0-9 ()-]{7,20}$")

/**
 * Checks the profile fields before they are saved.
 *
 * Name is required. Email and phone number are optional, but have to look
 * like an email or phone number if they are filled in.
 *
 * @param name the name the user typed
 * @param email the email the user typed
 * @param phone the phone number the user typed
 * @return the errors for each field
 */
fun validateProfile(name: String, email: String, phone: String): ProfileErrors {
    val nameError = if (name.isBlank()) "Name is required" else null

    val trimmedEmail = email.trim()
    val emailError = if (trimmedEmail.isNotEmpty() && !emailPattern.matches(trimmedEmail)) {
        "Enter a valid email"
    } else {
        null
    }

    val trimmedPhone = phone.trim()
    val phoneError = if (trimmedPhone.isNotEmpty() && !phonePattern.matches(trimmedPhone)) {
        "Enter a valid phone number"
    } else {
        null
    }

    return ProfileErrors(name = nameError, email = emailError, phone = phoneError)
}
