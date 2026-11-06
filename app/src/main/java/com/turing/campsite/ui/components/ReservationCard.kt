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
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import java.text.SimpleDateFormat
import java.util.Locale

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val CancelledColor = Color(0xFFB45345)

@Composable
fun ReservationCard(reservation: Reservation) {
    val statusText = when (reservation.status) {
        ReservationStatus.ACTIVE -> "Confirmed"
        ReservationStatus.COMPLETED -> "Past"
        ReservationStatus.CANCELLED -> "Cancelled"
        ReservationStatus.REJECTED -> "Rejected"
    }

    val statusColor = when (reservation.status) {
        ReservationStatus.ACTIVE -> PrimaryColor
        ReservationStatus.COMPLETED -> SecondaryText
        ReservationStatus.CANCELLED, ReservationStatus.REJECTED -> CancelledColor
    }

    val dateText = reservation.startDate?.let { timestamp ->
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(timestamp.toDate())
    } ?: "No date"

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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Color(0xFF82967A),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(statusColor, CircleShape)
                        )

                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            color = statusColor
                        )
                    }

                    Text(
                        text = reservation.campsiteId,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Campsite Location",
                        fontSize = 11.sp,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = CardBorder)

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
                        text = dateText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )
                }

                Text(
                    text = "2 nights",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Text(
                text = "2 adults · 1 tent · Woodland",
                fontSize = 11.sp,
                color = SecondaryText
            )

            when (reservation.status) {
                ReservationStatus.ACTIVE -> {
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

                ReservationStatus.COMPLETED,
                ReservationStatus.CANCELLED,
                ReservationStatus.REJECTED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = reservation.id,
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