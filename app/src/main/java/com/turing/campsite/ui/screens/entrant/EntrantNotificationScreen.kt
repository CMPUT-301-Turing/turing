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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.NotificationCard
import com.turing.campsite.ui.viewmodel.NotificationViewModel

private val PageBackground = Color(0xFFF6F4ED)
private val SecondaryText = Color(0xFF7A8079)

@Composable
fun EntrantNotificationScreen(
    notificationViewModel: NotificationViewModel
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val notifications = notificationViewModel.notifications

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            notificationViewModel.loadForUser(currentUserId)
        }
    }

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

        item {
            NotificationSectionHeader(
                title = "Recent",
                trailingText = "${notifications.count { !it.read }} unread"
            )
        }

        items(notifications, key = { it.id }) { notification ->
            NotificationCard(notification)
        }
    }
}

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