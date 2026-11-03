
package com.turing.campsite.ui.screens.owner

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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
private val RejectBackground = Color(0xFFF7E9E5)
private val RejectColor = Color(0xFFB45345)
private val CancelledBackground = Color(0xFFF7E9E5)
private val CompletedBackground = Color(0xFFE9EBE5)

/**
 * Available reservation categories.
 */
private enum class ReservationSection(
    val label: String,
    val heading: String,
    val description: String
) {
    ACTIVE(
        label = "Active",
        heading = "Upcoming stays",
        description = "Reject cancels the booking and issues a refund to the entrant."
    ),
    PAST(
        label = "Past",
        heading = "Completed stays",
        description = "These stays have ended. You can still message each entrant."
    ),
    CANCELLED(
        label = "Cancelled",
        heading = "Cancelled bookings",
        description = "These bookings are no longer active. Message entrants for any follow-up."
    )
}

/**
 * Temporary reservation information for the owner.
 */
private data class OwnerReservationPreview(
    val id: String,
    val entrantName: String,
    val campsiteName: String,
    val dateRange: String,
    val section: ReservationSection
)

/**
 * Sample reservations matching the three Figma views.
 */
private val sampleReservations = listOf(
    // Active reservations
    OwnerReservationPreview(
        id = "reservation-1",
        entrantName = "Oliver Bennett",
        campsiteName = "Pinewood Retreat",
        dateRange = "21–23 Oct 2026",
        section = ReservationSection.ACTIVE
    ),
    OwnerReservationPreview(
        id = "reservation-2",
        entrantName = "Amelia Clarke",
        campsiteName = "Riverbend Glade",
        dateRange = "23–25 Oct 2026",
        section = ReservationSection.ACTIVE
    ),
    OwnerReservationPreview(
        id = "reservation-3",
        entrantName = "Noah Williams",
        campsiteName = "Riverbend Glade",
        dateRange = "30–31 Oct 2026",
        section = ReservationSection.ACTIVE
    ),

    // Past reservations
    OwnerReservationPreview(
        id = "reservation-4",
        entrantName = "Sophie Turner",
        campsiteName = "Pinewood Retreat",
        dateRange = "17–19 Oct 2026",
        section = ReservationSection.PAST
    ),
    OwnerReservationPreview(
        id = "reservation-5",
        entrantName = "Ethan Brooks",
        campsiteName = "Willowbank Meadow",
        dateRange = "8–10 Oct 2026",
        section = ReservationSection.PAST
    ),
    OwnerReservationPreview(
        id = "reservation-6",
        entrantName = "Isla Morgan",
        campsiteName = "Riverbend Glade",
        dateRange = "25–27 Sep 2026",
        section = ReservationSection.PAST
    ),

    // Cancelled reservations
    OwnerReservationPreview(
        id = "reservation-7",
        entrantName = "Grace Wilson",
        campsiteName = "Pinewood Retreat",
        dateRange = "24–26 Oct 2026",
        section = ReservationSection.CANCELLED
    ),
    OwnerReservationPreview(
        id = "reservation-8",
        entrantName = "Jack Davies",
        campsiteName = "Willowbank Meadow",
        dateRange = "26–28 Sep 2026",
        section = ReservationSection.CANCELLED
    ),
    OwnerReservationPreview(
        id = "reservation-9",
        entrantName = "Freya Ellis",
        campsiteName = "Riverbend Glade",
        dateRange = "18–20 Sep 2026",
        section = ReservationSection.CANCELLED
    )
)

/**
 * Displays reservations for campsites owned by the user.
 *
 * One screen supports Active, Past, and Cancelled views.
 * Message and Reject actions are handled through callbacks.
 *
 * Reservation data is temporary.
 */
@Composable
fun OwnerCampsiteHistoryScreen(
    onBackClick: () -> Unit,
    onMessageClick: (String) -> Unit,
    onRejectClick: (String) -> Unit
) {
    var selectedSection by rememberSaveable {
        mutableStateOf(ReservationSection.ACTIVE)
    }

    val visibleReservations = sampleReservations.filter {
        it.section == selectedSection
    }

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
                    contentDescription = "Back to campsite details",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Campsite Details",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reservation category tabs
        ReservationSectionTabs(
            selectedSection = selectedSection,
            onSectionSelected = {
                selectedSection = it
            }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Section heading and booking count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedSection.heading,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = "${visibleReservations.size} bookings",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "All campsites · As of 20 Oct 2026",
            fontSize = 11.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filtered reservation list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = visibleReservations,
                key = { it.id }
            ) { reservation ->
                OwnerReservationCard(
                    reservation = reservation,
                    onMessageClick = {
                        onMessageClick(reservation.id)
                    },
                    onRejectClick = {
                        onRejectClick(reservation.id)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = SecondaryText
                    )

                    Text(
                        text = selectedSection.description,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Tabs for switching between reservation states.
 */
@Composable
private fun ReservationSectionTabs(
    selectedSection: ReservationSection,
    onSectionSelected: (ReservationSection) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReservationSection.entries.forEach { section ->
            val selected = selectedSection == section

            Surface(
                onClick = {
                    onSectionSelected(section)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = if (selected) {
                    PrimaryColor
                } else {
                    CardBackground
                },
                border = if (selected) {
                    null
                } else {
                    BorderStroke(1.dp, CardBorder)
                }
            ) {
                Text(
                    text = section.label,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 10.dp
                    ),
                    fontSize = 12.sp,
                    fontWeight = if (selected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },
                    color = if (selected) {
                        Color.White
                    } else {
                        PrimaryColor
                    },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * Displays a reservation for the campsite owner.
 *
 * Active reservations include Message and Reject buttons.
 * Past and cancelled reservations include Message only.
 */
@Composable
private fun OwnerReservationCard(
    reservation: OwnerReservationPreview,
    onMessageClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = reservation.entrantName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    Text(
                        text = reservation.campsiteName,
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )

                    Text(
                        text = reservation.dateRange,
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }

                ReservationStatusBadge(reservation.section)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = CardBorder
            ) {}

            // Reservation actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReservationActionButton(
                    label = "Message",
                    icon = Icons.Outlined.ChatBubbleOutline,
                    onClick = onMessageClick,
                    modifier = Modifier.weight(1f)
                )

                if (reservation.section == ReservationSection.ACTIVE) {
                    ReservationActionButton(
                        label = "Reject",
                        icon = Icons.Outlined.Close,
                        onClick = onRejectClick,
                        destructive = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Displays the reservation status badge.
 */
@Composable
private fun ReservationStatusBadge(
    section: ReservationSection
) {
    val label: String
    val backgroundColor: Color
    val textColor: Color

    when (section) {
        ReservationSection.ACTIVE -> {
            label = "Confirmed"
            backgroundColor = ChipBackground
            textColor = PrimaryColor
        }

        ReservationSection.PAST -> {
            label = "Completed"
            backgroundColor = CompletedBackground
            textColor = PrimaryColor
        }

        ReservationSection.CANCELLED -> {
            label = "Cancelled"
            backgroundColor = CancelledBackground
            textColor = RejectColor
        }
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

/**
 * Shared action button within a reservation card.
 *
 * Destructive actions use a red background and text.
 */
@Composable
private fun ReservationActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false
) {
    val backgroundColor = if (destructive) {
        RejectBackground
    } else {
        ChipBackground
    }

    val contentColor = if (destructive) {
        RejectColor
    } else {
        PrimaryColor
    }

    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(7.dp),
        color = backgroundColor
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                7.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = contentColor
            )

            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}