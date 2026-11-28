/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */
package com.turing.campsite.ui.screens.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.data.model.User
import java.text.SimpleDateFormat
import java.util.Locale

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val DeleteColor = Color(0xFFB45345)
private val DeleteBorder = Color(0xFFE8CFC8)

/**
 * The currently selected view for a user.
 *
 * This controls the UI presentation, not the
 * user's authentication or account permissions.
 */
enum class UserView {
    ENTRANT,
    OWNER
}

/**
 * Shared profile screen for entrants and campsite owners.
 *
 * Displays the user's saved details and profile settings. Empty details
 * show a "Not added" hint so the user knows to fill them in. The user is
 * null while the profile is still loading. The notifications switch calls
 * onNotificationsChange with the new setting (US 01.10.01).
 *
 * The current view determines which role the user
 * can switch to and whether a back button is shown.
 *
 * Navigation is handled through callbacks.
 *
 * @param onSwitchToAdminClick opens the admin view; when null the
 * admin option is not shown
 */
@Composable
fun UserProfileScreen(
    currentView: UserView,
    user: User? = null,
    onNotificationsChange: (enabled: Boolean) -> Unit = {},
    onEditProfileClick: () -> Unit,
    onPolicyClick: () -> Unit = {},
    onSwitchViewClick: (() -> Unit)?,
    onBackClick: () -> Unit,
    onSwitchToAdminClick: (() -> Unit)? = null,
    onDeleteProfileConfirm: () -> Unit = {}
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val switchTitle: String
    val switchDescription: String

    when (currentView) {
        UserView.ENTRANT -> {
            switchTitle = "Switch to campsite owner view"
            switchDescription = "Manage your campsites in the owner view."
        }

        UserView.OWNER -> {
            switchTitle = "Switch to entrant view"
            switchDescription = "Browse and reserve campsites in the entrant view."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Owner view has no bottom navigation bar,
            // so provide a back button.
            if (currentView == UserView.OWNER) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to owner home",
                        modifier = Modifier.size(22.dp),
                        tint = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = "Your profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Text(
            text = "A little space for you.",
            fontSize = 12.sp,
            color = SecondaryText
        )

        // Profile information
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileField(
                    label = "Name",
                    value = profileValue(user, user?.name),
                    emphasized = true
                )

                HorizontalDivider(color = CardBorder)

                ProfileField(
                    label = "Email",
                    value = profileValue(user, user?.email)
                )

                HorizontalDivider(color = CardBorder)

                ProfileField(
                    label = "Phone number",
                    value = profileValue(user, user?.phone)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sign-up date",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )

                    Text(
                        text = user?.let {
                            SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                                .format(it.createdAt.toDate())
                        } ?: "Loading...",
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )
                }

                // Edit profile button
                Surface(
                    onClick = onEditProfileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryColor
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Edit profile",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Profile settings
        ProfileOption(
            title = "Identify profile by device",
            description = "Use this device to identify your profile.",
            icon = Icons.Outlined.PhoneAndroid
        )

        // On means the user gets notifications, off means they opted out
        val notificationsOn = user?.notificationsEnabled ?: true
        ProfileToggleOption(
            title = "Receive notifications",
            description = if (notificationsOn) {
                "Notifications are on. Turn off to opt out of all notifications."
            } else {
                "You opted out. You won't get any notifications."
            },
            icon = Icons.Outlined.NotificationsNone,
            checked = notificationsOn,
            // Disabled until the profile loads, so the switch can't save the wrong user
            enabled = user != null,
            onCheckedChange = onNotificationsChange
        )

        // Offer only views authorized by the signed-in profile.
        if (onSwitchViewClick != null) {
            ProfileOption(
                title = switchTitle,
                description = switchDescription,
                icon = Icons.Outlined.SwapHoriz,
                onClick = onSwitchViewClick
            )
        }

        if (onSwitchToAdminClick != null) {
            ProfileOption(
                title = "Switch to admin view",
                description = "Manage campsites and profiles in the admin view.",
                icon = Icons.Outlined.ManageAccounts,
                onClick = onSwitchToAdminClick
            )
        }

        ProfileOption(
            title = "View app policy and guidelines",
            description = "Read our privacy policy and community guidelines.",
            icon = Icons.Outlined.Article,
            onClick = onPolicyClick
        )

        ProfileOption(
            title = "Delete profile",
            description = "Permanently remove your profile.",
            icon = Icons.Outlined.DeleteOutline,
            destructive = true,
            onClick = { showDeleteDialog = true }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete your profile?") },
            text = { Text("Your details and settings will be removed. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteProfileConfirm()
                }) {
                    Text("Delete", color = DeleteColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = PrimaryColor)
                }
            }
        )
    }
}

/**
 * Picks the text to show for one profile detail.
 *
 * @param user the loaded user, or null while loading
 * @param value the detail to show
 * @return "Loading..." before the user loads, "Not added" if the detail is empty
 */
private fun profileValue(user: User?, value: String?): String = when {
    user == null -> "Loading..."
    value.isNullOrBlank() -> "Not added"
    else -> value
}

/**
 * Displays a profile setting that is turned on or off with a switch.
 *
 * The whole row can be tapped, not just the switch.
 */
@Composable
private fun ProfileToggleOption(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Toggleable surface, so screen readers announce it as on or off
        Surface(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(9.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = PrimaryColor
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    color = PrimaryColor
                )

                Switch(
                    checked = checked,
                    // The row handles the click so taps on the text work too
                    onCheckedChange = null,
                    enabled = enabled,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = PrimaryColor,
                        uncheckedThumbColor = SecondaryText,
                        uncheckedTrackColor = PageBackground,
                        uncheckedBorderColor = SecondaryText
                    )
                )
            }
        }

        Text(
            text = description,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 11.sp,
            color = SecondaryText
        )
    }
}

/**
 * Displays a labelled profile field.
 */
@Composable
private fun ProfileField(
    label: String,
    value: String,
    emphasized: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = SecondaryText
        )

        Text(
            text = value,
            fontSize = if (emphasized) 16.sp else 13.sp,
            fontWeight = if (emphasized) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            color = PrimaryColor
        )
    }
}

/**
 * Displays a profile settings option.
 *
 * The onClick callback is optional so that features
 * without implementations can remain visual placeholders.
 */
@Composable
private fun ProfileOption(
    title: String,
    description: String,
    icon: ImageVector,
    destructive: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val contentColor = if (destructive) DeleteColor else PrimaryColor
    val borderColor = if (destructive) DeleteBorder else CardBorder

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .then(clickModifier),
            shape = RoundedCornerShape(9.dp),
            color = if (destructive) PageBackground else CardBackground,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (destructive) {
                    Arrangement.Center
                } else {
                    Arrangement.Start
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = contentColor
                )

                if (!destructive) {
                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = PrimaryColor
                    )
                }
            }
        }

        Text(
            text = description,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 11.sp,
            color = SecondaryText,
            textAlign = if (destructive) {
                TextAlign.Center
            } else {
                TextAlign.Start
            }
        )
    }
}