package com.turing.campsite

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.ui.graphics.vector.ImageVector



@Composable
fun AdminHomeScreen(navController: NavController) {

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .padding(top = 27.dp)
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Admin Home",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )

                // Settings button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = CardColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            navController.navigate("AdminProfileSetting")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ManageAccounts,
                        contentDescription = "Settings",
                        tint = PrimaryColor,
                        modifier = Modifier.size(27.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Admin menu items
            AdminMenuItem(
                title = "Browse Campsites",
                icon = Icons.Outlined.Terrain,
                onClick = {
                    navController.navigate("AdminCampsite")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminMenuItem(
                title = "Browse Profiles",
                icon = Icons.Outlined.PeopleOutline,
                onClick = {
                    navController.navigate("AdminProfile")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminMenuItem(
                title = "Browse Images",
                icon = Icons.Outlined.Image,
                onClick = {
                    navController.navigate("AdminBrowseImage")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminMenuItem(
                title = "Message Logs",
                icon = Icons.AutoMirrored.Outlined.Chat,
                onClick = {
                    navController.navigate("AdminMessageLog")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminMenuItem(
                title = "Notification Logs",
                icon = Icons.Outlined.NotificationsNone,
                onClick = {
                    navController.navigate("AdminNotificationLog")
                }
            )
        }
    }
}

@Composable
fun AdminMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .background(
                color = CardColor,
                shape = RoundedCornerShape(11.dp)
            )
            .clickable { onClick() }
            .then(
                Modifier
            )
            .padding(horizontal = 21.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = PrimaryColor,
            modifier = Modifier.size(29.dp)
        )

        Spacer(modifier = Modifier.width(23.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )
    }
}