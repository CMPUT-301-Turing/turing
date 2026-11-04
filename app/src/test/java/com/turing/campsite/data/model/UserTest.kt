package com.turing.campsite.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserTest {

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
}
