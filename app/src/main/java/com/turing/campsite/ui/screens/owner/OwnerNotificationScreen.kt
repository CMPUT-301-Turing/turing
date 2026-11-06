package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke

import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.NotificationCard
import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.NotificationType
import com.turing.campsite.data.model.Lottery
import com.google.firebase.Timestamp

import java.util.Date


private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)


/**
 * Temporary history of notifications sent by the owner.
 */
private val sampleNotifications = listOf(
    Notification(
        id = "notification-1",
        title = "Registration reminder",
        body = "Registration closes 14 Nov at 23:59 GMT. There's still time to update your entry.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000)),
        type = NotificationType.OWNER_MESSAGE,
        read = false
    ),
    Notification(
        id = "notification-2",
        title = "Your November stay",
        body = "Planning ahead? The booking period for this lottery is 16–23 Nov 2026.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 172800000)),
        type = NotificationType.OWNER_MESSAGE,
        read = false
    ),
    Notification(
        id = "notification-3",
        title = "How the draw works",
        body = "The automatic draw is on 15 Nov at 00:00 GMT, with 10 winners across both campsites.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 259200000)),
        type = NotificationType.OWNER_MESSAGE,
        read = false
    ),
    Notification(
        id = "notification-4",
        title = "The waitlist is open",
        body = "Welcome to our November lottery. Registration is open until 14 Nov 2026.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 345600000)),
        type = NotificationType.OWNER_MESSAGE,
        read = false
    )
)

/**
 * Temporary lottery option for notification creation.
 */
private val sampleLotteries = listOf(
    Lottery(
        id = "november-lottery",
        campsiteID = "Pinewood Retreat",
        winnerCount = 12,
        status = "open"
    )
)

/**
 * Displays the owner's sent notification history.
 *
 * The floating action button opens a notification
 * composer using a Material 3 bottom sheet.
 */
@Composable
fun OwnerNotificationScreen(
    onBackClick: () -> Unit
) {
    var showNewNotificationSheet by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        contentDescription = "Back to owner home",
                        modifier = Modifier.size(22.dp),
                        tint = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Notifications",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Notification history description
            Text(
                text = "As of 10 Nov 2026",
                fontSize = 12.sp,
                color = SecondaryText
            )

            Text(
                text = "Sample sent history · November lottery",
                fontSize = 12.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Sent notifications heading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sent notifications",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Text(
                    text = "${sampleNotifications.size} sent",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable sent notification history
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(
                    items = sampleNotifications,
                    key = { it.id }
                ) { notification ->
                    NotificationCard(
                        notification = notification,
                        isSentNotification = true
                    )
                }
            }
        }

        // Open notification composer
        FloatingActionButton(
            onClick = {
                showNewNotificationSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            shape = RoundedCornerShape(50),
            containerColor = PrimaryColor,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create notification",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    // Notification composer bottom sheet
    if (showNewNotificationSheet) {
        NewNotificationSheet(
            onDismissRequest = {
                showNewNotificationSheet = false
            }
        )
    }
}

/**
 * Bottom sheet for composing a notification to
 * entrants in a selected campsite lottery.
 *
 * Lottery data is temporary. Sending notifications
 * is not implemented yet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewNotificationSheet(
    onDismissRequest: () -> Unit
) {
    var selectedLottery by remember {
        mutableStateOf(sampleLotteries.first())
    }

    var lotteryDropdownExpanded by remember {
        mutableStateOf(false)
    }

    var message by rememberSaveable {
        mutableStateOf(
            "A quick reminder: registration closes on 14 Nov at 23:59 GMT. " +
                    "The automatic draw takes place on 15 Nov at 00:00 GMT. Good luck!"
        )
    }

    var sendAttempted by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = PageBackground,
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp
        ),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sheet header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Notification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close notification composer",
                        modifier = Modifier.size(22.dp),
                        tint = PrimaryColor
                    )
                }
            }

            Text(
                text = "Unsent draft · Sample snapshot, 10 Nov 2026",
                fontSize = 11.sp,
                color = SecondaryText
            )

            // Lottery selection
            Text(
                text = "Campsite lottery waitlist",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    onClick = {
                        lotteryDropdownExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(21.dp),
                            tint = PrimaryColor
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = selectedLottery.campsiteID,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryColor
                            )

                            Text(
                                text = "November 2026 · Combined lottery",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.ExpandMore,
                            contentDescription = "Choose lottery",
                            modifier = Modifier.size(20.dp),
                            tint = PrimaryColor
                        )
                    }
                }

                // Temporary lottery dropdown
                DropdownMenu(
                    expanded = lotteryDropdownExpanded,
                    onDismissRequest = {
                        lotteryDropdownExpanded = false
                    }
                ) {
                    sampleLotteries.forEach { lottery ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = lottery.campsiteID,
                                        fontSize = 12.sp,
                                        color = PrimaryColor
                                    )

                                    Text(
                                        text = "November 2026 · Combined lottery",
                                        fontSize = 11.sp,
                                        color = SecondaryText
                                    )
                                }
                            },
                            onClick = {
                                selectedLottery = lottery
                                lotteryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "Send goes to all ${selectedLottery.winnerCount} " +
                        "waitlist entrants in this combined lottery.",
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Notification message
            Text(
                text = "Message",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                    sendAttempted = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 125.dp),
                minLines = 4,
                maxLines = 7,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = PrimaryColor
                ),
                shape = RoundedCornerShape(9.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground
                )
            )

            Text(
                text = "Your update will be sent as a notification.",
                fontSize = 11.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Send action (backend not implemented)
            Button(
                onClick = {
                    sendAttempted = true
                },
                enabled = message.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                )
            ) {
                Text(
                    text = "Send",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (sendAttempted) {
                Text(
                    text = "Sending notifications is not implemented yet.",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }
    }
}