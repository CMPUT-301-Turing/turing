
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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

import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)
private val PendingBackground = Color(0xFFF2E8D0)
private val PendingText = Color(0xFF836A3C)

/**
 * Temporary waitlist entrant data.
 */
private data class WaitlistEntrantPreview(
    val name: String,
    val initials: String,
    val joinedDate: String
)

/**
 * Sample waitlist matching the Figma design.
 */
private val sampleWaitlist = listOf(
    WaitlistEntrantPreview("Amelia Clarke", "AC", "10 Nov 2026"),
    WaitlistEntrantPreview("Oliver Bennett", "OB", "09 Nov 2026"),
    WaitlistEntrantPreview("Isla Morgan", "IM", "09 Nov 2026"),
    WaitlistEntrantPreview("Noah Patel", "NP", "08 Nov 2026"),
    WaitlistEntrantPreview("Sophie Chen", "SC", "08 Nov 2026"),
    WaitlistEntrantPreview("Harry Wilson", "HW", "07 Nov 2026"),
    WaitlistEntrantPreview("Maya Thompson", "MT", "06 Nov 2026"),
    WaitlistEntrantPreview("Liam Carter", "LC", "05 Nov 2026"),
    WaitlistEntrantPreview("Emma Brooks", "EB", "04 Nov 2026"),
    WaitlistEntrantPreview("Alex Morgan", "AM", "03 Nov 2026"),
    WaitlistEntrantPreview("Grace Lee", "GL", "02 Nov 2026"),
    WaitlistEntrantPreview("James Parker", "JP", "01 Nov 2026")
)

/**
 * Read-only details for an owner's campsite lottery.
 *
 * Displays lottery configuration, automatic draw status,
 * and waitlist entrants.
 *
 * Data is currently static.
 * Navigation is handled through callbacks.
 */
@Composable
fun OwnerLotteryDetailsScreen(
    onBackClick: () -> Unit,
    onSendNotificationClick: () -> Unit
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
                text = "Lottery Details",
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
            item {
                Text(
                    text = "As of 10 Nov 2026",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            item {
                LotteryOverviewSection()
            }

            item {
                LotterySystemStatusSection()
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onSendNotificationClick,
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
                        text = "Send an update to all 12 waitlist entrants.",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            item {
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
                                text = "${sampleWaitlist.size} / 100",
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
                        text = "Maximum waitlist enabled · Newest first",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            // Individual waitlist entrants
            items(
                items = sampleWaitlist,
                key = { it.name }
            ) { entrant ->
                WaitlistEntrantCard(entrant)
            }
        }
    }
}

/**
 * Displays participating campsites and lottery dates.
 */
@Composable
private fun LotteryOverviewSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lottery Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ChipBackground
                ) {
                    Text(
                        text = "• Active",
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryColor
                    )
                }
            }

            // Participating campsites
            LotteryCampsiteRow(
                name = "Pinewood Retreat",
                location = "New Forest, Hampshire"
            )

            LotteryCampsiteRow(
                name = "Riverbend Glade",
                location = "Dartmoor, Devon"
            )

            HorizontalDivider(color = CardBorder)

            // Registration period
            LotteryPeriodRow(
                label = "Registration Period",
                dateRange = "01–14 Nov 2026"
            )

            // Booking period
            LotteryPeriodRow(
                label = "Booking Period",
                dateRange = "16–23 Nov 2026"
            )

            // Number of winners
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = ChipBackground
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                            tint = PrimaryColor
                        )

                        Text(
                            text = "10 winners",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )
                    }

                    Text(
                        text = "Across both campsites",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

/**
 * A campsite participating in the lottery.
 */
@Composable
private fun LotteryCampsiteRow(
    name: String,
    location: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    ChipBackground,
                    RoundedCornerShape(9.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Terrain,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = PrimaryColor
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = location,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}

/**
 * Reusable display for a lottery date period.
 */
@Composable
private fun LotteryPeriodRow(
    label: String,
    dateRange: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = PrimaryColor
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = SecondaryText
            )

            Text(
                text = dateRange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryColor
            )
        }
    }
}

/**
 * Displays the current automatic lottery draw status.
 */
@Composable
private fun LotterySystemStatusSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section heading
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                    tint = PrimaryColor
                )

                Text(
                    text = "System Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )
            }

            Text(
                text = "Automatic Winner Draw: Pending",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            // Pending status badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PendingBackground
            ) {
                Text(
                    text = "• Awaiting registration close",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 11.sp,
                    color = PendingText
                )
            }

            // Timeline dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "01 Nov · Opened",
                    fontSize = 11.sp,
                    color = SecondaryText
                )

                Text(
                    text = "10 Nov · Today",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            }

            // Static progress indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        ChipBackground,
                        RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight()
                        .background(
                            PrimaryColor,
                            RoundedCornerShape(4.dp)
                        )
                )
            }

            Text(
                text = "○ Auto draw · 15 Nov 2026, 00:00 GMT",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = "Registration closes 14 Nov at 23:59 GMT. " +
                        "The system will then select 10 winners, " +
                        "before booking opens on 16 Nov.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = SecondaryText
            )
        }
    }
}

/**
 * Displays a participant currently on the lottery waitlist.
 */
@Composable
private fun WaitlistEntrantCard(
    entrant: WaitlistEntrantPreview
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
                    text = entrant.initials,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entrant.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Text(
                    text = "Joined ${entrant.joinedDate}",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }
    }
}