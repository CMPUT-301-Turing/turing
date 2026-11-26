/* Checks which fixture profiles expose owner removal and which ID confirmation forwards. */
package com.turing.campsite.ui.screens.admin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.turing.campsite.data.model.User
import com.turing.campsite.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class AdminOwnerModerationScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun onlyEligibleProfileHasActionAndConfirmationUsesItsId() {
        val owner = User(id = "owner-c", name = "Casey")
        val admin = User(id = "admin-a", name = "Alex", roles = listOf(UserRole.OWNER, UserRole.ADMIN))
        var removed: String? = null
        compose.setContent {
            AdminBrowseProfilesScreen(
                onBackClick = {}, profiles = listOf(owner, admin), ownerRemovalEnabled = true,
                onRemoveOwner = { removed = it.id }
            )
        }
        compose.onNodeWithContentDescription("Remove owner access for Alex").assertDoesNotExist()
        compose.onNodeWithContentDescription("Remove owner access for Casey").performClick()
        compose.onNodeWithText("Cancel").performClick()
        compose.runOnIdle { assertNull(removed) }
        compose.onNodeWithContentDescription("Remove owner access for Casey").performClick()
        compose.onNodeWithText("Remove owner access").performClick()
        compose.runOnIdle { assertEquals("owner-c", removed) }
    }
}
