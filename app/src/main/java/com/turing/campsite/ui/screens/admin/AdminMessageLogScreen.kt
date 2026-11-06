package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.saveable.rememberSaveable
import com.turing.campsite.ui.components.matchesSearch
import com.turing.campsite.ui.components.SearchBar
import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.FilterChip
import com.turing.campsite.data.model.Message
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val ChipBackground = Color(0xFFE8EFE3)

/**
 * Available message log filters.
 */
private enum class MessageLogFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active Reservations"),
    INACTIVE("Inactive Reservations")
}

/**
 * Sample conversations for the administrator's message log.
 */
private val sampleMessageLogs = listOf(
    Message(
        id = "message-1",
        body = "Perfect, see you at 3 PM tomorrow! Thanks for your help.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 3600000)),
        fromUserId = "owner-1"
    ),
    Message(
        id = "message-2",
        body = "The gate code is in your arrival guide.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 7200000)),
        fromUserId = "owner-2"
    ),
    Message(
        id = "message-3",
        body = "Thanks! We'll bring our own firewood.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 10800000)),
        fromUserId = "owner-3"
    ),
    Message(
        id = "message-4",
        body = "Yes, parking is beside your reserved pitch.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 14400000)),
        fromUserId = "owner-4"
    ),
    Message(
        id = "message-5",
        body = "Thank you again for staying with us!",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 18000000)),
        fromUserId = "owner-5"
    ),
    Message(
        id = "message-6",
        body = "Glad you enjoyed your stay.",
        createdAt = Timestamp(Date(System.currentTimeMillis() - 21600000)),
        fromUserId = "owner-6"
    )
)

private fun formatTime(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
}

/**
 * Administrator's read-only message log.
 *
 * Displays conversations associated with reservations.
 * Filtering operates on temporary sample data.
 * Selecting a conversation invokes the navigation callback.
 */
@Composable
fun AdminMessageLogScreen(
    onBackClick: () -> Unit,
    onConversationClick: (String) -> Unit
) {
    var selectedFilter by remember {
        mutableStateOf(MessageLogFilter.ALL)
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Messages whose sender, receiver or text contains what was typed.
    val visibleLogs = sampleMessageLogs.filter {
        matchesSearch(searchQuery, it.fromUserId, it.toUserId, it.body)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header and back navigation
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

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Message Logs",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reservation filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MessageLogFilter.entries.forEach { filter ->
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

        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search sender or receiver"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Date and sorting information
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

        // Conversation list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = visibleLogs,
                key = { it.id }
            ) { message ->
                MessageLogCard(
                    message = message,
                    onClick = {
                        onConversationClick(message.id)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "All conversations · Read-only audit log",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 11.sp,
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Displays a clickable conversation summary in the message log.
 *
 * Clicking the card opens the full conversation history.
 */
@Composable
private fun MessageLogCard(
    message: Message,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Owner and conversation timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Owner: John Smith →",
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = formatTime(message.createdAt),
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                }

                Text(
                    text = "Entrant: Jane Doe",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )

                // Associated campsite and reservation dates
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ChipBackground
                ) {
                    Text(
                        text = "Banff Campsite · Oct 25–27",
                        modifier = Modifier.padding(
                            horizontal = 6.dp,
                            vertical = 3.dp
                        ),
                        fontSize = 10.sp,
                        color = PrimaryColor
                    )
                }

                // Most recent message
                Text(
                    text = message.body,
                    fontSize = 11.sp,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}