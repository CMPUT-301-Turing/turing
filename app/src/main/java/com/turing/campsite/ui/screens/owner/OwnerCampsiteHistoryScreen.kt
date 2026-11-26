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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

import com.google.firebase.Timestamp
import com.turing.campsite.PrimaryColor
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import com.turing.campsite.data.model.reservationDayStart
import com.turing.campsite.data.model.statusForOwnerHistory

import java.text.SimpleDateFormat
import java.util.Locale

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)
private val RejectBackground = Color(0xFFF7E9E5)
private val RejectColor = Color(0xFFB45345)
private val CancelledBackground = Color(0xFFF7E9E5)
private val CompletedBackground = Color(0xFFE9EBE5)

private fun formatDate(timestamp: Timestamp?): String {
    if (timestamp == null) return "No date"

    val date = timestamp.toDate()

    val format = SimpleDateFormat("dd MMM", Locale.getDefault())
    return format.format(date)
}

/**
 * Displays reservations for campsites owned by the user. The caller supplies the owner-scoped list.
 */
@Composable
fun OwnerCampsiteHistoryScreen(
    onBackClick: () -> Unit,
    onMessageClick: (String) -> Unit,
    onRejectClick: (String) -> Unit,
    reservations: List<Reservation>,
    isLoading: Boolean = false,
    statusMessage: String? = null
) {
    var selectedSection by rememberSaveable {
        mutableStateOf(OwnerReservationTab.ACTIVE)
    }

    var pendingRejectionId by rememberSaveable { mutableStateOf<String?>(null) }
    val dayStart = reservationDayStart()
    val pendingRejection = reservations.firstOrNull {
        it.id == pendingRejectionId && it.statusForOwnerHistory(dayStart) == ReservationStatus.ACTIVE
    }

    if (pendingRejection != null && !isLoading) {
        AlertDialog(
            onDismissRequest = { pendingRejectionId = null },
            title = { Text("Reject reservation?") },
            text = { Text("This booking will no longer be active.") },
            confirmButton = {
                TextButton(onClick = {
                    pendingRejectionId = null
                    onRejectClick(pendingRejection.id)
                }) { Text("Reject reservation") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRejectionId = null }) { Text("Keep booking") }
            }
        )
    }

    val visibleReservations = selectedSection.select(reservations, dayStart)

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
                text = "Reservations",
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
                text = if (isLoading) "Loading…" else "${visibleReservations.size} bookings",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "All your campsites",
            fontSize = 11.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (statusMessage != null) {
            Text(text = statusMessage, color = RejectColor, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Filtered reservation list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (visibleReservations.isEmpty() && !isLoading && statusMessage == null) {
                item {
                    Text(text = "No bookings in this tab.", color = SecondaryText)
                }
            }

            items(
                items = visibleReservations,
                key = { it.id }
            ) { reservation ->
                OwnerReservationCard(
                    reservation = reservation,
                    status = reservation.statusForOwnerHistory(dayStart),
                    onMessageClick = {
                        onMessageClick(reservation.id)
                    },
                    onRejectClick = {
                        pendingRejectionId = reservation.id
                    },
                    canReject = !isLoading && reservation.id.isNotBlank()
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
    selectedSection: OwnerReservationTab,
    onSectionSelected: (OwnerReservationTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OwnerReservationTab.entries.forEach { section ->
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
 */
@Composable
private fun OwnerReservationCard(
    reservation: Reservation,
    status: ReservationStatus,
    onMessageClick: () -> Unit,
    onRejectClick: () -> Unit,
    canReject: Boolean
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
                        text = reservation.entrantId,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    Text(
                        text = reservation.campsiteId.takeIf { it.isNotBlank() }
                            ?.let { "Campsite ref: $it" } ?: "Campsite unavailable",
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )

                    Text(
                        text = formatDate(reservation.startDate),
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }

                ReservationStatusBadge(status)
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


                if (canReject && status == ReservationStatus.ACTIVE) {
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
    status: ReservationStatus
) {
    val label: String
    val backgroundColor: Color
    val textColor: Color

    when (status) {
        ReservationStatus.ACTIVE -> {
            label = "Confirmed"
            backgroundColor = ChipBackground
            textColor = PrimaryColor
        }
        ReservationStatus.COMPLETED -> {
            label = "Completed"
            backgroundColor = CompletedBackground
            textColor = PrimaryColor
        }
        else -> { // CANCELLED or REJECTED
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