
package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val AvatarBackground = Color(0xFFE8EFE3)
private val LogoutBackground = Color(0xFFF6E7E1)
private val LogoutBorder = Color(0xFFEDD5CF)
private val LogoutColor = Color(0xFFA54E44)

/**
 * Administrator profile and settings screen.
 *
 * Profile information is temporary.
 * Navigation between views is handled through callbacks.
 * Settings and logout functionality will be implemented later.
 */
@Composable
fun AdminProfileScreen(
    onBackClick: () -> Unit,
    onSwitchToOwnerClick: () -> Unit,
    onSwitchToEntrantClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to admin home",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Admin Profile/Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Administrator profile summary
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(AvatarBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        modifier = Modifier.size(27.dp),
                        tint = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Morgan Ellis",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "morgan.ellis@campmail.com",
                    fontSize = 11.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AvatarBackground
                ) {
                    Text(
                        text = "Administrator",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        fontSize = 10.sp,
                        color = PrimaryColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Switch View
        Text(
            text = "Switch View",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        AdminSwitchViewRow(
            label = "Switch to Campsite Owner",
            icon = Icons.Outlined.Terrain,
            onClick = onSwitchToOwnerClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        AdminSwitchViewRow(
            label = "Switch to Entrant",
            icon = Icons.Outlined.PersonOutline,
            onClick = onSwitchToEntrantClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Settings
        Text(
            text = "Settings",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column {
                AdminSettingsRow(
                    label = "Notification Preferences",
                    icon = Icons.Outlined.NotificationsNone
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = CardBorder
                )

                AdminSettingsRow(
                    label = "App Policy & Guidelines",
                    icon = Icons.Outlined.Description
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = CardBorder
                )

                AdminSettingsRow(
                    label = "Appearance",
                    icon = Icons.Outlined.Palette,
                    trailingText = "Light"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Log out (visual placeholder)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(9.dp),
            color = LogoutBackground,
            border = BorderStroke(1.dp, LogoutBorder)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = LogoutColor
                )

                Text(
                    text = "Log Out",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = LogoutColor
                )
            }
        }
    }
}

/**
 * Switches from the administrator view to another role view.
 */
@Composable
private fun AdminSwitchViewRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 15.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = PrimaryColor
            )

            Text(
                text = label,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                color = PrimaryColor
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = PrimaryColor
            )
        }
    }
}

/**
 * Displays an administrator settings option.
 *
 * Settings navigation is not implemented yet.
 */
@Composable
private fun AdminSettingsRow(
    label: String,
    icon: ImageVector,
    trailingText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = PrimaryColor
        )

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = PrimaryColor
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = SecondaryText
        )
    }
}