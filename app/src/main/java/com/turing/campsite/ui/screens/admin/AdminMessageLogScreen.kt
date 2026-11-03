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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.FilterChip

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val ChipBackground = Color(0xFFE8EFE3)

/**
 * Reservation state associated with a conversation.
 */
private enum class MessageReservationStatus {
    ACTIVE,
    INACTIVE
}

/**
 * Available message log filters.
 */
private enum class MessageLogFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active Reservations"),
    INACTIVE("Inactive Reservations")
}

/**
 * Temporary conversation information.
 */
private data class MessageLogPreview(
    val id: String,
    val owner: String,
    val entrant: String,
    val campsite: String,
    val reservationDates: String,
    val lastMessage: String,
    val timestamp: String,
    val reservationStatus: MessageReservationStatus
)

/**
 * Sample conversations for the administrator's message log.
 */
private val sampleMessageLogs = listOf(
    MessageLogPreview(
        id = "message-1",
        owner = "John Smith",
        entrant = "Jane Doe",
        campsite = "Banff Campsite",
        reservationDates = "Oct 25–27",
        lastMessage = "Perfect, see you at 3 PM tomorrow! Thanks for your help.",
        timestamp = "09:24",
        reservationStatus = MessageReservationStatus.ACTIVE
    ),
    MessageLogPreview(
        id = "message-2",
        owner = "Maya Patel",
        entrant = "Alex Morgan",
        campsite = "Pinewood",
        reservationDates = "Oct 26–28",
        lastMessage = "The gate code is in your arrival guide.",
        timestamp = "08:50",
        reservationStatus = MessageReservationStatus.ACTIVE
    ),
    MessageLogPreview(
        id = "message-3",
        owner = "Liam Wilson",
        entrant = "Sophie Chen",
        campsite = "Lake Louise",
        reservationDates = "Oct 25–26",
        lastMessage = "Thanks! We'll bring our own firewood.",
        timestamp = "08:12",
        reservationStatus = MessageReservationStatus.ACTIVE
    ),
    MessageLogPreview(
        id = "message-4",
        owner = "Emma Brooks",
        entrant = "Noah Lee",
        campsite = "Cedar Creek",
        reservationDates = "Oct 24–26",
        lastMessage = "Yes, parking is beside your reserved pitch.",
        timestamp = "07:45",
        reservationStatus = MessageReservationStatus.ACTIVE
    ),
    MessageLogPreview(
        id = "message-5",
        owner = "Ava Nguyen",
        entrant = "Ethan Reed",
        campsite = "Jasper Ridge",
        reservationDates = "Oct 10–12",
        lastMessage = "Thank you again for staying with us!",
        timestamp = "07:20",
        reservationStatus = MessageReservationStatus.INACTIVE
    ),
    MessageLogPreview(
        id = "message-6",
        owner = "Oliver Clark",
        entrant = "Grace Kim",
        campsite = "Forest Edge",
        reservationDates = "Oct 4–6",
        lastMessage = "Glad you enjoyed your stay.",
        timestamp = "06:55",
        reservationStatus = MessageReservationStatus.INACTIVE
    )
)

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

    val visibleLogs = sampleMessageLogs.filter { message ->
        when (selectedFilter) {
            MessageLogFilter.ALL -> true

            MessageLogFilter.ACTIVE ->
                message.reservationStatus == MessageReservationStatus.ACTIVE

            MessageLogFilter.INACTIVE ->
                message.reservationStatus == MessageReservationStatus.INACTIVE
        }
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
                    text = "Search sender or receiver",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

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
    message: MessageLogPreview,
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
                        text = "Owner: ${message.owner} →",
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                }

                Text(
                    text = "Entrant: ${message.entrant}",
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
                        text = "${message.campsite} · ${message.reservationDates}",
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
                    text = message.lastMessage,
                    fontSize = 11.sp,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

