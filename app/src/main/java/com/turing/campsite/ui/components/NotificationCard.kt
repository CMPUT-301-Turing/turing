package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor


import com.turing.campsite.data.model.Notification
import com.turing.campsite.data.model.NotificationType
import java.text.SimpleDateFormat
import java.util.Locale

private val CardBackground = Color(0xFFFCFBF7)
private val EarlierCardBackground = Color(0xFFF3F2EC)
private val CardBorder = Color(0xFFDFE2D8)
private val IconBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

@Composable
fun NotificationCard(
    notification: Notification,
    isSentNotification: Boolean = false
) {

    val isUnread = !notification.read

    val backgroundColor = if (
        isSentNotification || isUnread
    ) {
        CardBackground
    } else {
        EarlierCardBackground
    }


    val icon = when (notification.type) {
        NotificationType.LOTTERY_WON -> Icons.Outlined.EmojiEvents
        NotificationType.LOTTERY_LOST -> Icons.Outlined.ConfirmationNumber
        NotificationType.RESERVATION_CONFIRMED -> Icons.Outlined.EventAvailable
        NotificationType.RESERVATION_CANCELLED -> Icons.Outlined.EventBusy
        NotificationType.OWNER_MESSAGE -> Icons.Outlined.NotificationsNone
    }

    val timestampText = notification.createdAt?.let {
        SimpleDateFormat("dd MMM\nHH:mm", Locale.getDefault()).format(it.toDate())
    } ?: ""


    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (isSentNotification) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(IconBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PrimaryColor
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (isSentNotification) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = notification.title,
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = timestampText,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            textAlign = TextAlign.End,
                            color = SecondaryText
                        )
                    }

                    Spacer(modifier = Modifier.size(2.dp))

                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = IconBackground
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 7.dp,
                                vertical = 4.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ConfirmationNumber,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = PrimaryColor
                            )

                            Text(
                                text = "Campsite Lottery",
                                fontSize = 10.sp,
                                color = PrimaryColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    Text(
                        text = notification.title,
                        fontSize = 13.sp,
                        fontWeight = if (isUnread) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        },
                        color = PrimaryColor
                    )

                    Text(
                        text = "Campsite Lottery",
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )
                }

                Text(
                    text = notification.body,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = SecondaryText
                )

                if (!isSentNotification) {
                    Text(
                        text = timestampText,
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                }
            }

            if (!isSentNotification && isUnread) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(6.dp)
                        .background(PrimaryColor, CircleShape)
                )
            }
        }
    }
}