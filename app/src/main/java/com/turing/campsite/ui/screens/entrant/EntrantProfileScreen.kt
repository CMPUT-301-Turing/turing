
package com.turing.campsite.ui.screens.entrant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
private val DeleteColor = Color(0xFFB45345)
private val DeleteBorder = Color(0xFFE8CFC8)

/**
 * Entrant profile screen.
 *
 * Displays temporary profile information and settings.
 * Shared bottom navigation is managed by AppScaffold.
 */
@Composable
fun EntrantProfileScreen(
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Text(
            text = "Your profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

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
                    label = "Username",
                    value = "alex_outdoors",
                    emphasized = true
                )

                HorizontalDivider(color = CardBorder)

                ProfileField(
                    label = "Email",
                    value = "alex.outdoors@example.com"
                )

                HorizontalDivider(color = CardBorder)

                ProfileField(
                    label = "Phone number",
                    value = "+44 7700 900123"
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
                        text = "12 June 2026",
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )
                }

                // Edit profile button (visual placeholder)
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

        ProfileOption(
            title = "Opt out of notifications",
            description = "Notifications are on. Opt out to stop alerts.",
            icon = Icons.Outlined.NotificationsNone
        )

        ProfileOption(
            title = "Switch to campsite owner view",
            description = "Manage your campsites in the owner view.",
            icon = Icons.Outlined.SwapHoriz
        )

        ProfileOption(
            title = "View app policy and guidelines",
            description = "Read our privacy policy and community guidelines.",
            icon = Icons.Outlined.Article
        )

        ProfileOption(
            title = "Delete profile",
            description = "Permanently remove your profile.",
            icon = Icons.Outlined.DeleteOutline,
            destructive = true
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
 * Currently visual only; callbacks can be added later.
 */
@Composable
private fun ProfileOption(
    title: String,
    description: String,
    icon: ImageVector,
    destructive: Boolean = false
) {
    val contentColor = if (destructive) DeleteColor else PrimaryColor
    val borderColor = if (destructive) DeleteBorder else CardBorder

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
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
                androidx.compose.ui.text.style.TextAlign.Center
            } else {
                androidx.compose.ui.text.style.TextAlign.Start
            }
        )
    }
}
