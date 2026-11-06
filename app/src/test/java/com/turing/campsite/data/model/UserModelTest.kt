package com.turing.campsite.data.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Checks user defaults, role membership, and independent profile and notification-preference edits.
 *
 * Exercises only model behavior; no persistence, UI, or server authorization is tested.
 */
class UserModelTest {
    private val fixed = Timestamp(1_000L, 123)
    private val later = Timestamp(2_000L, 456)

    /** Verifies empty profile fields, ENTRANT and OWNER roles, enabled notifications, and the supplied creation time. */
    @Test fun defaultsProvideOrdinaryRolesAndOptionalContactFields() {
        val subject = User(createdAt = fixed)
        assertEquals("", subject.id)
        assertEquals("", subject.name)
        assertEquals("", subject.email)
        assertEquals("", subject.phone)
        assertEquals(listOf(UserRole.ENTRANT, UserRole.OWNER), subject.roles)
        assertTrue(subject.notificationsEnabled)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that role membership matches the assigned list for every subset of defined roles. */
    @Test fun allRoleCombinationsUseExactMembership() {
        val roles = UserRole.values()
        for (mask in 0 until (1 shl roles.size)) {
            val assigned = roles.filterIndexed { index, _ -> mask and (1 shl index) != 0 }
            val subject = User(roles = assigned, createdAt = fixed)
            for (role in roles) assertEquals("mask=$mask role=$role", role in assigned, subject.hasRole(role))
        }
    }

    /** Verifies that clearing roles removes all memberships and assigning ADMIN exposes only that role. */
    @Test fun roleRemovalAndRestorationAreVisibleImmediately() {
        val subject = User(createdAt = fixed)
        subject.roles = emptyList()
        for (role in UserRole.values()) assertFalse(subject.hasRole(role))
        subject.roles = listOf(UserRole.ADMIN)
        assertTrue(subject.hasRole(UserRole.ADMIN))
        assertFalse(subject.hasRole(UserRole.OWNER))
        assertFalse(subject.hasRole(UserRole.ENTRANT))
    }

    /** Verifies that duplicate OWNER roles do not grant ADMIN or ENTRANT membership. */
    @Test fun duplicateRolesDoNotGrantUnassignedRoles() {
        val subject = User(roles = listOf(UserRole.OWNER, UserRole.OWNER), createdAt = fixed)
        assertTrue(subject.hasRole(UserRole.OWNER))
        assertFalse(subject.hasRole(UserRole.ADMIN))
        assertFalse(subject.hasRole(UserRole.ENTRANT))
    }

    /** Verifies that editing profile fields and notification preferences preserves identity, roles, and creation time. */
    @Test fun editingProfilePreservesIdentityRolesAndCreationTime() {
        val subject = User(id = "user-1", createdAt = fixed)
        subject.name = "Camper"
        subject.email = "camper@example.invalid"
        subject.phone = "5550100"
        subject.notificationsEnabled = false
        assertEquals("Camper", subject.name)
        assertEquals("camper@example.invalid", subject.email)
        assertEquals("5550100", subject.phone)
        assertFalse(subject.notificationsEnabled)
        assertEquals("user-1", subject.id)
        assertEquals(listOf(UserRole.ENTRANT, UserRole.OWNER), subject.roles)
        assertEquals(fixed, subject.createdAt)
    }

    /** Verifies that notifications can be disabled and re-enabled on the same model. */
    @Test fun notificationPreferenceCanBeTurnedOffAndBackOn() {
        val subject = User(createdAt = fixed)
        subject.notificationsEnabled = false
        assertFalse(subject.notificationsEnabled)
        subject.notificationsEnabled = true
        assertTrue(subject.notificationsEnabled)
    }

    // Tests retained from UserTest, including its per-test fixture setup.


    private lateinit var user: User

    // Set up a fresh user before every single test
    @Before
    fun setUp() {
        user = User(id = "abc123", name = "Test User")
    }

    @Test
    fun hasRole_newUser_entrantAndOwner() {
        assertTrue(user.hasRole(UserRole.ENTRANT))
        assertTrue(user.hasRole(UserRole.OWNER))

        assertFalse(user.hasRole(UserRole.ADMIN)) // admin is never given by default
    }

    @Test
    fun hasRole_adminAdded_keepsOtherRoles() {
        user.roles = user.roles + UserRole.ADMIN

        assertTrue(user.hasRole(UserRole.ADMIN))
        assertTrue(user.hasRole(UserRole.ENTRANT))
        assertTrue(user.hasRole(UserRole.OWNER))

        assertEquals(3, user.roles.size) // entrant, owner, admin
    }

    @Test
    fun hasRole_roleRemoved_returnsFalse() {
        user.roles = listOf(UserRole.ENTRANT) // user gave up the owner role

        assertTrue(user.hasRole(UserRole.ENTRANT))
        assertFalse(user.hasRole(UserRole.OWNER))
    }

    @Test
    fun newUser_defaults_notificationsOn() {
        assertTrue(user.notificationsEnabled)

        assertEquals("", user.email) // email is optional, empty until filled in
        assertEquals("", user.phone)
    }

    @Test
    fun hasRole_emptyRoles_returnsFalseForEveryRole() {
        user.roles = emptyList()
        assertFalse(user.hasRole(UserRole.ENTRANT))
        assertFalse(user.hasRole(UserRole.OWNER))
        assertFalse(user.hasRole(UserRole.ADMIN))
    }

    @Test
    fun hasRole_ownerOnly_doesNotGrantOtherRoles() {
        user.roles = listOf(UserRole.OWNER)
        assertTrue(user.hasRole(UserRole.OWNER))
        assertFalse(user.hasRole(UserRole.ENTRANT))
        assertFalse(user.hasRole(UserRole.ADMIN))
    }

    @Test
    fun hasRole_adminOnly_doesNotGrantOtherRoles() {
        user.roles = listOf(UserRole.ADMIN)
        assertTrue(user.hasRole(UserRole.ADMIN))
        assertFalse(user.hasRole(UserRole.ENTRANT))
        assertFalse(user.hasRole(UserRole.OWNER))
    }
}
