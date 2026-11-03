package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.FilterChip

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val IconBackground = Color(0xFFE8EFE3)

/**
 * Categories used to filter notification logs.
 */
private enum class NotificationCategory {
    LOTTERY,
    RESERVATION,
    CANCELLATION,
    ANNOUNCEMENT
}

/**
 * Notification filters shown in the UI.
 */
private enum class NotificationLogFilter(val label: String) {
    ALL("All"),
    LOTTERY("Lottery"),
    RESERVATIONS("Reservations"),
    CANCELLATIONS("Cancellations"),
    ANNOUNCEMENTS("Announcements")
}

/**
 * Temporary notification log data.
 */
private data class NotificationLogPreview(
    val id: String,
    val recipient: String,
    val message: String,
    val timestamp: String,
    val category: NotificationCategory,
    val icon: ImageVector
)

/**
 * Sample notification history matching the Figma design.
 */
private val sampleNotificationLogs = listOf(
    NotificationLogPreview(
        id = "notification-1",
        recipient = "John Doe",
        message = "You have won the lottery for Banff Campsite!",
        timestamp = "Oct 24, 2026 · 10:42 AM",
        category = NotificationCategory.LOTTERY,
        icon = Icons.Outlined.EmojiEvents
    ),
    NotificationLogPreview(
        id = "notification-2",
        recipient = "Maya Bennett",
        message = "You were not selected in the Banff Campsite lottery.",
        timestamp = "Oct 24, 2026 · 10:40 AM",
        category = NotificationCategory.LOTTERY,
        icon = Icons.Outlined.Close
    ),
    NotificationLogPreview(
        id = "notification-3",
        recipient = "Sophie Chen",
        message = "Your Pinewood reservation for Nov 6–8 is confirmed.",
        timestamp = "Oct 24, 2026 · 10:32 AM",
        category = NotificationCategory.RESERVATION,
        icon = Icons.Outlined.EventAvailable
    ),
    NotificationLogPreview(
        id = "notification-4",
        recipient = "All campers",
        message = "Winter bookings open on Nov 1. Plan your next escape.",
        timestamp = "Oct 24, 2026 · 10:15 AM",
        category = NotificationCategory.ANNOUNCEMENT,
        icon = Icons.Outlined.NotificationsNone
    ),
    NotificationLogPreview(
        id = "notification-5",
        recipient = "Alex Morgan",
        message = "Your Lake Louise reservation for Nov 2–4 is cancelled.",
        timestamp = "Oct 24, 2026 · 9:58 AM",
        category = NotificationCategory.CANCELLATION,
        icon = Icons.Outlined.EventBusy
    )
)

/**
 * Admin notification history screen.
 *
 * Displays a read-only notification audit log.
 * Category filtering is implemented with sample data.
 * Search and database integration are placeholders.
 */
@Composable
fun AdminNotificationLogScreen(
    onBackClick: () -> Unit
) {
    var selectedFilter by remember {
        mutableStateOf(NotificationLogFilter.ALL)
    }

    val visibleLogs = sampleNotificationLogs.filter { notification ->
        when (selectedFilter) {
            NotificationLogFilter.ALL -> true

            NotificationLogFilter.LOTTERY ->
                notification.category == NotificationCategory.LOTTERY

            NotificationLogFilter.RESERVATIONS ->
                notification.category == NotificationCategory.RESERVATION

            NotificationLogFilter.CANCELLATIONS ->
                notification.category == NotificationCategory.CANCELLATION

            NotificationLogFilter.ANNOUNCEMENTS ->
                notification.category == NotificationCategory.ANNOUNCEMENT
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
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

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Notification Logs",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NotificationLogFilter.entries.forEach { filter ->
                FilterChip(
                    label = filter.label,
                    selected = selectedFilter == filter,
                    onClick = {
                        selectedFilter = filter
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search bar (visual placeholder)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = PrimaryColor
                )

                Text(
                    text = "Search by name or email",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Date and sort order
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "October 24, 2026",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryColor
            )

            Text(
                text = "Newest first",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notification timeline
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = visibleLogs,
                key = { it.id }
            ) { notification ->
                NotificationLogEntry(notification)
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Sent notifications · Read-only audit log",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 11.sp,
                    color = SecondaryText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Displays one notification entry on the timeline.
 */
@Composable
private fun NotificationLogEntry(
    notification: NotificationLogPreview
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Timeline icon and connecting line
        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(CardBorder)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(24.dp)
                    .background(IconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = PrimaryColor
                )
            }
        }

        // Notification card
        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 7.dp),
            shape = RoundedCornerShape(9.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(11.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Sent to: ${notification.recipient}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = PrimaryColor,
                    lineHeight = 16.sp
                )

                Text(
                    text = notification.timestamp,
                    fontSize = 10.sp,
                    color = SecondaryText
                )
            }
        }
    }
}
