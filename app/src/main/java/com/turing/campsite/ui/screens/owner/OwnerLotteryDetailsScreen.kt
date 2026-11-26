
package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.Timestamp
import com.turing.campsite.PrimaryColor
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.data.model.LotteryEntry
import java.text.DateFormat

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)
private val PendingBackground = Color(0xFFF2E8D0)
private val PendingText = Color(0xFF836A3C)

private fun displayDate(value: Timestamp?): String =
    value?.toDate()?.let { DateFormat.getDateInstance().format(it) } ?: "Not set"

/**
 * Read-only details for an owner's campsite lottery.
 *
 * Displays lottery configuration, automatic draw status,
 * and waitlist entrants.
 *
 * Navigation is handled through callbacks.
 */
@Composable
fun OwnerLotteryDetailsScreen(
    onBackClick: () -> Unit,
    onSendNotificationClick: (() -> Unit)? = null,
    lottery: Lottery? = null,
    entries: List<LotteryEntry> = emptyList(),
    statusMessage: String? = null,
    readOnly: Boolean = false,
    campsiteName: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to campsite details",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (readOnly) "Lottery Waitlist" else "Lottery Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        // Scrollable lottery details
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (campsiteName != null) item { Text(campsiteName, fontWeight = FontWeight.SemiBold) }
            if (statusMessage != null) {
                item { Text(statusMessage, color = PrimaryColor) }
            }
            if (lottery != null) {
                item {
                    val status = if (lottery.isRegistrationOpen()) "Registration open" else
                        if (lottery.status.equals("open", ignoreCase = true)) "Registration not open now" else
                            lottery.status.replaceFirstChar { it.uppercase() }
                    Text("Lottery status: $status", color = PrimaryColor)
                    Text("Winners to draw: ${lottery.winnerCount}")
                    Text("Registration: ${displayDate(lottery.registrationStart)} to ${displayDate(lottery.registrationEnd)}")
                    Text("Booking: ${displayDate(lottery.bookingStart)} to ${displayDate(lottery.bookingEnd)}")
                }
            }

            if (onSendNotificationClick != null) item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onSendNotificationClick,
                        enabled = lottery != null && entries.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(9.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Send Notification to Waitlist",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "${entries.size} waitlist entrants",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            if (!readOnly || lottery != null) item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Waitlist Entrants",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = ChipBackground
                        ) {
                            Text(
                                text = if (lottery != null && lottery.waitlistCap > 0) "${entries.size} / ${lottery.waitlistCap}" else "${entries.size}",
                                modifier = Modifier.padding(
                                    horizontal = 11.dp,
                                    vertical = 6.dp
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryColor
                            )
                        }
                    }

                    Text(
                        text = if (lottery != null && lottery.waitlistCap > 0) "Newest first" else "No maximum set · Newest first",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            if (lottery != null && entries.isEmpty()) item { Text("No entrants have joined.") }

            // Individual waitlist entrants
            items(
                items = entries.sortedByDescending { it.joinedAt },
                key = { it.id }
            ) { entrant ->
                WaitlistEntrantCard(entrant)
            }
        }
    }
}

/**
 * Displays a participant currently on the lottery waitlist.
 */
@Composable
private fun WaitlistEntrantCard(
    entrant: LotteryEntry
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Initials avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(ChipBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entrant.entrantId.take(2).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entrant.entrantId.ifBlank { entrant.id },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Text(
                    text = "Joined ${displayDate(entrant.joinedAt)} · ${entrant.result.name.lowercase()}",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }
    }
}