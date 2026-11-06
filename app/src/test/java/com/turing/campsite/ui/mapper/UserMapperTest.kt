package com.turing.campsite.ui.mapper

import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserMapperTest {
    private lateinit var amelia: User

    @Before
    fun setUp() {
        amelia = User(
            id = "u1",
            name = "Amelia Clarke",
            email = "amelia@example.com",
            roles = listOf(UserRole.ENTRANT)
        )
    }

    @Test
    fun initialsOf_twoWordName_returnsBothFirstLetters() {
        assertEquals("AC", initialsOf("Amelia Clarke"))
    }

    @Test
    fun initialsOf_oneWordName_returnsOneLetter() {
        assertEquals("A", initialsOf("amelia")) // also made upper case
    }

    @Test
    fun initialsOf_threeWordName_usesFirstTwoWords() {
        assertEquals("AR", initialsOf("Amelia Rose Clarke"))
    }

    @Test
    fun initialsOf_blankName_returnsQuestionMark() {
        assertEquals("?", initialsOf("   "))
    }

    @Test
    fun toProfilePreview_entrant_copiesFields() {
        val profile = amelia.toProfilePreview()

        assertEquals("u1", profile.id)
        assertEquals("Amelia Clarke", profile.name)
        assertEquals("amelia@example.com", profile.email)
        assertEquals("Entrant", profile.role)
        assertEquals("AC", profile.initials)
    }

    @Test
    fun toProfilePreview_ownerAndEntrant_showsOwnerRole() {
        amelia.roles = listOf(UserRole.ENTRANT, UserRole.OWNER)

        assertEquals("Campsite Owner", amelia.toProfilePreview().role)
    }

    @Test
    fun toProfilePreview_admin_showsAdminRole() {
        amelia.roles = listOf(UserRole.ENTRANT, UserRole.OWNER, UserRole.ADMIN)

        assertEquals("Admin", amelia.toProfilePreview().role)
    }

    @Test
    fun toProfilePreview_userWithoutName_showsNoName() {
        amelia.name = ""

        val profile = amelia.toProfilePreview()

        assertEquals("No name", profile.name)
        assertEquals("?", profile.initials)
    }
}
