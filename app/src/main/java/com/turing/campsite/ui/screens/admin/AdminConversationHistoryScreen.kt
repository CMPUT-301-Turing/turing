package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.ConversationMessageBubble

import com.turing.campsite.data.model.Message
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

private val sampleMessages = listOf(
    Message(
        id = "message-1",
        body = "Hi Jane, just confirming your check-in time for tomorrow. When will you arrive?",
        fromUserId = "owner-id",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 1200000))
    ),
    Message(
        id = "message-2",
        body = "Hi John! We plan to arrive at 3 PM. Is parking close to the pitch?",
        fromUserId = "entrant-id",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 1000000))
    ),
    Message(
        id = "message-3",
        body = "Yes, parking is right beside pitch 12. I'll meet you at the gate at 3 PM.",
        fromUserId = "owner-id",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 800000))
    ),
    Message(
        id = "message-4",
        body = "Perfect, see you at 3 PM tomorrow! Thanks for your help.",
        fromUserId = "entrant-id",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 600000))
    )
)

private fun formatTime(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
}

@Composable
fun AdminConversationHistoryScreen(
    messages: List<Message> = sampleMessages,
    onBackClick: () -> Unit
) {
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
                    contentDescription = "Back to message logs",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Conversation",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (messages.isEmpty()) {
            Text(
                text = "Conversation not found.",
                fontSize = 14.sp,
                color = SecondaryText
            )
        } else {
            // Conversation summary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = CardBackground,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = PrimaryColor
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Owner: John Smith →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )

                        Text(
                            text = "Entrant: Jane Doe",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ChipBackground
                        ) {
                            Text(
                                text = "Banff Campsite · Oct 25–27",
                                modifier = Modifier.padding(
                                    horizontal = 7.dp,
                                    vertical = 4.dp
                                ),
                                fontSize = 11.sp,
                                color = PrimaryColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Conversation date and message count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "October 24, 2026",
                    fontSize = 13.sp,
                    color = PrimaryColor
                )

                Text(
                    text = "${messages.size} messages",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full conversation history
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    val isEntrant = message.fromUserId != "owner-id"

                    ConversationMessageBubble(
                        message = message.body,
                        timestamp = formatTime(message.createdAt),
                        senderLabel = if (isEntrant) {
                            "Entrant · Jane Doe"
                        } else {
                            "Owner · John Smith"
                        },
                        alignRight = isEntrant
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "End of conversation",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            6.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = PrimaryColor
                        )

                        Text(
                            text = "Read-only · Admins cannot send messages",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}