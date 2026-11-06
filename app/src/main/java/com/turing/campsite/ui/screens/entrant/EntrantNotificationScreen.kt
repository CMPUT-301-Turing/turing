package com.turing.campsite.ui.screens.entrant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.NotificationCard
import com.google.firebase.Timestamp
import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.NotificationType
import java.util.Date

private val PageBackground = Color(0xFFF6F4ED)
private val SecondaryText = Color(0xFF7A8079)

/** One hour in milliseconds, used to give the sample notifications a time. */
private const val HOUR = 3_600_000L

/**
 * Temporary notification data for displaying the screen.
 */
private val todayNotifications = listOf(
    Notification(
        id = "notification-1",
        title = "You won the lottery!",
        body = "13–15 Nov · A pitch is yours to book. " +
                "Accept your place by 21 Oct.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 1 * HOUR)),
        type = NotificationType.LOTTERY_WON,
        read = false
    ),
    Notification(
        id = "notification-2",
        title = "Lottery result",
        body = "6–8 Nov · You weren't selected this time. " +
                "Try a future draw.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 2 * HOUR)),
        type = NotificationType.LOTTERY_LOST,
        read = false
    )
)

private val earlierNotifications = listOf(
    Notification(
        id = "notification-3",
        title = "Cancellation confirmed",
        body = "6–8 Nov · Your reservation PR-1042 " +
                "has been cancelled.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 26 * HOUR)),
        type = NotificationType.RESERVATION_CANCELLED,
        read = true
    ),
    Notification(
        id = "notification-4",
        title = "Reservation confirmed",
        body = "6–8 Nov · Your pitch was booked. " +
                "Reservation PR-1042.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 72 * HOUR)),
        type = NotificationType.RESERVATION_CONFIRMED,
        read = true
    )
)

/**
 * Entrant notification screen.
 *
 * Displays static notification previews grouped by date.
 * Shared bottom navigation is managed by AppScaffold.
 */
@Composable
fun EntrantNotificationScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
        contentPadding = PaddingValues(
            horizontal = 22.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Notifications",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your next adventure, kept up to date.",
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Today's notifications
        item {
            NotificationSectionHeader(
                title = "Today",
                trailingText = "${todayNotifications.count { !it.read }} unread"
            )
        }

        items(todayNotifications) { notification ->
            NotificationCard(notification)
        }

        // Earlier notifications
        item {
            Spacer(modifier = Modifier.height(8.dp))

            NotificationSectionHeader(title = "Earlier")
        }

        items(earlierNotifications) { notification ->
            NotificationCard(notification)
        }
    }
}

/**
 * Displays a notification section heading.
 */
@Composable
private fun NotificationSectionHeader(
    title: String,
    trailingText: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}
