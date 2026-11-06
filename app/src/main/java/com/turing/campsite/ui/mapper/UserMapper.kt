package com.turing.campsite.ui.mapper

import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole
import com.turing.campsite.ui.screens.admin.ProfilePreview

/**
 * Builds the one or two letters shown in a profile circle.
 *
 * @param name the full name of the user
 * @return the first letter of the first two words in upper case, or "?" if the name is blank
 */
fun initialsOf(name: String): String {
    val words = name.trim().split(" ").filter { it.isNotEmpty() }
    if (words.isEmpty()) {
        return "?"
    }
    return words.take(2).joinToString("") { it.first().uppercase() }
}

/**
 * Turns a user from the database into the row shown on the admin profiles screen.
 *
 * The highest role is shown: admin, then campsite owner, then entrant.
 *
 * @return the profile row for this user
 */
fun User.toProfilePreview(): ProfilePreview {
    val role = when {
        hasRole(UserRole.ADMIN) -> "Admin"
        hasRole(UserRole.OWNER) -> "Campsite Owner"
        else -> "Entrant"
    }
    return ProfilePreview(
        name = name.ifBlank { "No name" },
        email = email,
        role = role,
        initials = initialsOf(name),
        id = id
    )
}
