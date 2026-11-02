
package com.turing.campsite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.turing.campsite.PrimaryColor
import com.turing.campsite.navigation.Routes

private val BarColor = Color(0xFFF5F3EA)
private val SelectedColor = Color(0xFFE7EDE3)

/**
 * Shared bottom navigation bar for entrant screens.
 *
 * Displays five navigation destinations and highlights
 * the currently selected route.
 *
 * @param selectedRoute Currently active navigation route.
 * @param onTabSelected Callback invoked when a tab is selected.
 */
@Composable
fun EntrantBottomBar(
    selectedRoute: String?,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BarColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EntrantBottomBarItem(
                route = Routes.ENTRANT_HOME,
                label = "Campsites",
                icon = Icons.Outlined.Terrain,
                selectedRoute = selectedRoute,
                onTabSelected = onTabSelected
            )

            EntrantBottomBarItem(
                route = Routes.ENTRANT_QR_CODE,
                label = "QR Code",
                icon = Icons.Outlined.CameraAlt,
                selectedRoute = selectedRoute,
                onTabSelected = onTabSelected
            )

            EntrantBottomBarItem(
                route = Routes.ENTRANT_CAMPSITE_LIST,
                label = "My Campsite Lists",
                icon = Icons.Outlined.Assignment,
                selectedRoute = selectedRoute,
                onTabSelected = onTabSelected
            )

            EntrantBottomBarItem(
                route = Routes.ENTRANT_NOTIFICATION,
                label = "Notifications",
                icon = Icons.Outlined.NotificationsNone,
                selectedRoute = selectedRoute,
                onTabSelected = onTabSelected
            )

            EntrantBottomBarItem(
                route = Routes.ENTRANT_PROFILE,
                label = "Profile",
                icon = Icons.Outlined.PersonOutline,
                selectedRoute = selectedRoute,
                onTabSelected = onTabSelected
            )
        }
    }
}

/**
 * Individual bottom navigation item.
 *
 * Displays an icon and highlights the selected destination
 * using a rounded background and a small indicator dot.
 */
@Composable
private fun RowScope.EntrantBottomBarItem(
    route: String,
    label: String,
    icon: ImageVector,
    selectedRoute: String?,
    onTabSelected: (String) -> Unit
) {
    val isSelected = selectedRoute == route

    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(48.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isSelected) SelectedColor
                    else Color.Transparent
                )
                .selectable(
                    selected = isSelected,
                    role = Role.Tab,
                    onClick = {
                        if (!isSelected) {
                            onTabSelected(route)
                        }
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryColor,
                modifier = Modifier.size(23.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(PrimaryColor, CircleShape)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
