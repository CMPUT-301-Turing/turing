
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

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Identifies the sender of a conversation message.
 */
private enum class MessageSender {
    OWNER,
    ENTRANT
}

/**
 * Temporary message data.
 */
private data class ConversationMessagePreview(
    val sender: MessageSender,
    val text: String,
    val timestamp: String
)

/**
 * Temporary conversation data.
 */
private data class ConversationPreview(
    val owner: String,
    val entrant: String,
    val campsite: String,
    val reservationDates: String,
    val date: String,
    val messages: List<ConversationMessagePreview>
)

/**
 * Sample conversations matching the IDs in AdminMessageLogScreen.
 */
private val sampleConversations = mapOf(
    "message-1" to ConversationPreview(
        owner = "John Smith",
        entrant = "Jane Doe",
        campsite = "Banff Campsite",
        reservationDates = "Oct 25–27",
        date = "October 24, 2026",
        messages = listOf(
            ConversationMessagePreview(
                sender = MessageSender.OWNER,
                text = "Hi Jane, just confirming your check-in time for tomorrow. When will you arrive?",
                timestamp = "09:10"
            ),
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "Hi John! We plan to arrive at 3 PM. Is parking close to the pitch?",
                timestamp = "09:14"
            ),
            ConversationMessagePreview(
                sender = MessageSender.OWNER,
                text = "Yes, parking is right beside pitch 12. I'll meet you at the gate at 3 PM.",
                timestamp = "09:18"
            ),
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "Perfect, see you at 3 PM tomorrow! Thanks for your help.",
                timestamp = "09:24"
            )
        )
    ),
    "message-2" to ConversationPreview(
        owner = "Maya Patel",
        entrant = "Alex Morgan",
        campsite = "Pinewood",
        reservationDates = "Oct 26–28",
        date = "October 24, 2026",
        messages = listOf(
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "Hi, where can I find the gate code?",
                timestamp = "08:45"
            ),
            ConversationMessagePreview(
                sender = MessageSender.OWNER,
                text = "The gate code is in your arrival guide.",
                timestamp = "08:50"
            )
        )
    ),
    "message-3" to ConversationPreview(
        owner = "Liam Wilson",
        entrant = "Sophie Chen",
        campsite = "Lake Louise",
        reservationDates = "Oct 25–26",
        date = "October 24, 2026",
        messages = listOf(
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "Are we allowed to bring firewood?",
                timestamp = "08:05"
            ),
            ConversationMessagePreview(
                sender = MessageSender.OWNER,
                text = "Yes, you can bring your own firewood.",
                timestamp = "08:08"
            ),
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "Thanks! We'll bring our own firewood.",
                timestamp = "08:12"
            )
        )
    ),
    "message-4" to ConversationPreview(
        owner = "Emma Brooks",
        entrant = "Noah Lee",
        campsite = "Cedar Creek",
        reservationDates = "Oct 24–26",
        date = "October 24, 2026",
        messages = listOf(
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "Is there parking near our pitch?",
                timestamp = "07:40"
            ),
            ConversationMessagePreview(
                sender = MessageSender.OWNER,
                text = "Yes, parking is beside your reserved pitch.",
                timestamp = "07:45"
            )
        )
    ),
    "message-5" to ConversationPreview(
        owner = "Ava Nguyen",
        entrant = "Ethan Reed",
        campsite = "Jasper Ridge",
        reservationDates = "Oct 10–12",
        date = "October 24, 2026",
        messages = listOf(
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "We had a great time at the campsite!",
                timestamp = "07:15"
            ),
            ConversationMessagePreview(
                sender = MessageSender.OWNER,
                text = "Thank you again for staying with us!",
                timestamp = "07:20"
            )
        )
    ),
    "message-6" to ConversationPreview(
        owner = "Oliver Clark",
        entrant = "Grace Kim",
        campsite = "Forest Edge",
        reservationDates = "Oct 4–6",
        date = "October 24, 2026",
        messages = listOf(
            ConversationMessagePreview(
                sender = MessageSender.ENTRANT,
                text = "We really enjoyed our visit.",
                timestamp = "06:50"
            ),
            ConversationMessagePreview(
                sender = MessageSender.OWNER,
                text = "Glad you enjoyed your stay.",
                timestamp = "06:55"
            )
        )
    )
)

/**
 * Displays the complete read-only conversation history.
 *
 * The conversation is selected using its ID.
 * Message bubbles are provided by a shared composable.
 */
@Composable
fun AdminConversationHistoryScreen(
    conversationId: String,
    onBackClick: () -> Unit
) {
    val conversation = sampleConversations[conversationId]

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

        if (conversation == null) {
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
                            text = "Owner: ${conversation.owner} →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )

                        Text(
                            text = "Entrant: ${conversation.entrant}",
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
                                text = "${conversation.campsite} · ${conversation.reservationDates}",
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
                    text = conversation.date,
                    fontSize = 13.sp,
                    color = PrimaryColor
                )

                Text(
                    text = "${conversation.messages.size} messages",
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
                items(conversation.messages) { message ->
                    val isEntrant = message.sender == MessageSender.ENTRANT

                    ConversationMessageBubble(
                        message = message.text,
                        timestamp = message.timestamp,
                        senderLabel = if (isEntrant) {
                            "Entrant · ${conversation.entrant}"
                        } else {
                            "Owner · ${conversation.owner}"
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