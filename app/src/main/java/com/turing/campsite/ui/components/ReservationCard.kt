package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val CancelledColor = Color(0xFFB45345)

/**
 * Possible reservation states.
 */
enum class ReservationStatus(val label: String) {
    CONFIRMED("Confirmed"),
    PAST("Past"),
    CANCELLED("Cancelled")
}

/**
 * Temporary data used to display a reservation card.
 */
data class ReservationPreview(
    val reservationId: String,
    val campsiteName: String,
    val location: String,
    val dateRange: String,
    val nights: Int,
    val adults: Int,
    val tents: Int,
    val environment: String,
    val status: ReservationStatus,
    val imageColor: Color
)

/**
 * Displays a reservation with status-specific actions.
 *
 * All action controls are visual placeholders for now.
 */
@Composable
fun ReservationCard(reservation: ReservationPreview) {
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
            // Campsite information
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder campsite image
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            reservation.imageColor,
                            RoundedCornerShape(7.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Terrain,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Reservation status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        val statusColor = when (reservation.status) {
                            ReservationStatus.CONFIRMED -> PrimaryColor
                            ReservationStatus.PAST -> SecondaryText
                            ReservationStatus.CANCELLED -> CancelledColor
                        }

                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(statusColor, CircleShape)
                        )

                        Text(
                            text = reservation.status.label,
                            fontSize = 10.sp,
                            color = statusColor
                        )
                    }

                    Text(
                        text = reservation.campsiteName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = reservation.location,
                        fontSize = 11.sp,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = CardBorder)

            // Reservation dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = PrimaryColor
                    )

                    Text(
                        text = reservation.dateRange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )
                }

                Text(
                    text = "${reservation.nights} nights",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            // Reservation details
            Text(
                text = "${reservation.adults} adults · " +
                        "${reservation.tents} tent · " +
                        reservation.environment,
                fontSize = 11.sp,
                color = SecondaryText
            )

            // Reservation actions
            when (reservation.status) {
                ReservationStatus.CONFIRMED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ReservationAction(
                                text = "Cancel reservation",
                                filled = false
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            ReservationAction(
                                text = "Download",
                                filled = true
                            )
                        }
                    }
                }

                ReservationStatus.PAST,
                ReservationStatus.CANCELLED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = reservation.reservationId,
                                fontSize = 12.sp,
                                color = PrimaryColor
                            )

                            Text(
                                text = "Reservation CSV",
                                fontSize = 10.sp,
                                color = SecondaryText
                            )
                        }

                        Box(modifier = Modifier.width(112.dp)) {
                            ReservationAction(
                                text = "Download",
                                filled = true
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays a reservation action.
 *
 * Currently visual only. Click callbacks will be added later.
 */
@Composable
private fun ReservationAction(
    text: String,
    filled: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        shape = RoundedCornerShape(7.dp),
        color = if (filled) PrimaryColor else CardBackground,
        border = if (filled) null else BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (filled) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(5.dp))
            }

            Text(
                text = text,
                fontSize = 11.sp,
                color = if (filled) Color.White else PrimaryColor
            )
        }
    }
}
