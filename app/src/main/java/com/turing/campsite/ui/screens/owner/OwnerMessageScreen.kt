
package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.ConversationBubbleStyle
import com.turing.campsite.ui.components.ConversationMessageBubble

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PageBackground = Color(0xFFF6F4ED)
private val ChipBackground = Color(0xFFE8EFE3)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Temporary message data.
 *
 * isFromOwner determines the message's alignment.
 */
private data class OwnerChatMessage(
    val id: String,
    val text: String,
    val timestamp: String,
    val isFromOwner: Boolean
)

/**
 * Sample conversation matching the Figma design.
 */
private val sampleMessages = listOf(
    OwnerChatMessage(
        id = "message-1",
        text = "Hi! We're looking forward to our stay on 23 Oct. Would arriving around 3 PM work?",
        timestamp = "9:32 AM",
        isFromOwner = false
    ),
    OwnerChatMessage(
        id = "message-2",
        text = "Hi Amelia! 3 PM is perfect. Arrival is between 2 PM and 6 PM. Just message me when you're close.",
        timestamp = "9:34 AM",
        isFromOwner = true
    ),
    OwnerChatMessage(
        id = "message-3",
        text = "Lovely, thank you! We'll have our dog with us. Is that okay?",
        timestamp = "9:36 AM",
        isFromOwner = false
    ),
    OwnerChatMessage(
        id = "message-4",
        text = "Of course! Please keep your dog on a lead. Looking forward to welcoming you on the 23rd.",
        timestamp = "9:38 AM",
        isFromOwner = true
    ),
    OwnerChatMessage(
        id = "message-5",
        text = "Will do. See you on the 23rd!",
        timestamp = "9:40 AM",
        isFromOwner = false
    )
)

/**
 * Messaging screen between a campsite owner and entrant.
 *
 * Uses the shared ConversationMessageBubble in CHAT mode.
 *
 * Sending messages updates local state only.
 * Database synchronization will be implemented later.
 */
@Composable
fun OwnerMessageScreen(
    onBackClick: () -> Unit,
    entrantName: String = "Amelia Clarke",
    campsiteName: String = "Riverbend Glade",
    reservationDates: String = "23–25 Oct 2026"
) {
    val messages = remember {
        mutableStateListOf<OwnerChatMessage>().apply {
            addAll(sampleMessages)
        }
    }

    var messageInput by rememberSaveable {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()

    // Scroll to the most recent message.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    // Add an outgoing message to the local conversation.
    fun sendMessage() {
        val text = messageInput.trim()

        if (text.isNotEmpty()) {
            val timestamp = SimpleDateFormat(
                "h:mm a",
                Locale.getDefault()
            ).format(Date())

            messages.add(
                OwnerChatMessage(
                    id = "local-${messages.size}",
                    text = text,
                    timestamp = timestamp,
                    isFromOwner = true
                )
            )

            messageInput = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .imePadding()
    ) {
        // Conversation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to reservations",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = entrantName,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        // Reservation information
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(9.dp),
            color = ChipBackground
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = PrimaryColor
                )

                Text(
                    text = "Regarding: $campsiteName • $reservationDates",
                    fontSize = 12.sp,
                    color = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Date separator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = CardBorder
            )

            Text(
                text = "20 Oct 2026",
                fontSize = 11.sp,
                color = SecondaryText
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = CardBorder
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Conversation history
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                ConversationMessageBubble(
                    message = message.text,
                    timestamp = message.timestamp,
                    senderLabel = "",
                    alignRight = message.isFromOwner,
                    style = ConversationBubbleStyle.CHAT
                )
            }
        }

        // Bottom message composer
        OwnerMessageComposer(
            message = messageInput,
            onMessageChange = {
                messageInput = it
            },
            onSendClick = {
                sendMessage()
            }
        )
    }
}

/**
 * Fixed message input and send button.
 *
 * The parent screen manages the draft text and
 * handles the send action.
 */
@Composable
private fun OwnerMessageComposer(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column {
        HorizontalDivider(color = CardBorder)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Message input field
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(24.dp),
                color = PageBackground,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = message,
                        onValueChange = onMessageChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            color = PrimaryColor
                        ),
                        cursorBrush = SolidColor(PrimaryColor),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                onSendClick()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (message.isEmpty()) {
                                    Text(
                                        text = "Type a message...",
                                        fontSize = 13.sp,
                                        color = SecondaryText
                                    )
                                }

                                innerTextField()
                            }
                        }
                    )
                }
            }

            // Send button
            IconButton(
                onClick = onSendClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        PrimaryColor,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    modifier = Modifier.size(22.dp),
                    tint = Color.White
                )
            }
        }
    }
}