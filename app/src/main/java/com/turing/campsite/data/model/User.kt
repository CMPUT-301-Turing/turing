package com.turing.campsite.data.model

import com.google.firebase.Timestamp

/**
 * The roles a user can have in the app.
 *
 * A regular user is both an entrant and an owner and switches between the two
 * views. Admin is added on top for admin accounts.
 */
enum class UserRole { ENTRANT, OWNER, ADMIN }

/**
 * Represents one document in the "users" collection.
 *
 * The document id is the Firebase Auth uid of the phone, so there is no
 * username or password. A user can hold several roles at once.
 *
 * @property id the Firestore document id, same as the Firebase Auth uid
 * @property name the display name of the user
 * @property email the contact email, optional
 * @property phone the contact phone number, optional
 * @property roles the roles this user has, entrant and owner by default
 */
data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var roles: List<UserRole> = listOf(UserRole.ENTRANT, UserRole.OWNER),
) {
    /**
     * Checks whether the user has a given role.
     *
     * @param role the role to look for
     * @return true if the role is in the user's roles list, false otherwise
     */
    fun hasRole(role: UserRole): Boolean = role in roles
}
