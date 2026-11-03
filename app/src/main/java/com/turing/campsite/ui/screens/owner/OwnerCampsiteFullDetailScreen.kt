package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Sample booked dates for October 2026.
 */
private val bookedDates = setOf(23, 24, 25, 30, 31)

/**
 * Detailed campsite information for the owner.
 *
 * Displays static sample data for Riverbend Glade.
 * Navigation is handled through callbacks.
 */
@Composable
fun OwnerCampsiteFullDetailScreen(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onQRCodeClick: () -> Unit,
    onEditLotteryClick: () -> Unit,
    onLotteryClick: () -> Unit,
    onReservationsClick: () -> Unit
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
                    contentDescription = "Back to owner home",
                    tint = PrimaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Campsite Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        // Scrollable campsite details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CampsiteHeroSection()

            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main campsite actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CampsiteAction(
                        label = "Edit Campsite",
                        icon = Icons.Outlined.Edit,
                        onClick = onEditClick,
                        modifier = Modifier.weight(1f)
                    )

                    CampsiteAction(
                        label = "QR Code",
                        icon = Icons.Outlined.QrCode2,
                        onClick = onQRCodeClick,
                        modifier = Modifier.weight(1f)
                    )

                    CampsiteAction(
                        label = "Edit Lottery",
                        icon = Icons.Outlined.ConfirmationNumber,
                        onClick = onEditLotteryClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                BookedDatesSection()

                CampsiteDetailsSection()

                CampsiteRulesSection()

                CampsiteViewSection(
                    onLotteryClick = onLotteryClick,
                    onReservationsClick = onReservationsClick
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Campsite cover image and title.
 *
 * A gradient is used until campsite photographs
 * are loaded from storage.
 */
@Composable
private fun CampsiteHeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF829A7A),
                        Color(0xFF59765A),
                        Color(0xFF243B30)
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.Terrain,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.Center),
            tint = Color.White.copy(alpha = 0.45f)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Riverbend Glade",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )

                Text(
                    text = "Dartmoor, Devon",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Circular action shortcut displayed below the cover image.
 */
@Composable
private fun CampsiteAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(ChipBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = PrimaryColor
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = PrimaryColor
        )
    }
}

/**
 * Common card layout for campsite detail sections.
 */
@Composable
private fun CampsiteDetailCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

/**
 * Read-only calendar showing reserved dates.
 *
 * October 2026 starts on a Thursday, so three
 * empty slots precede October 1 in a Monday-first grid.
 */
@Composable
private fun BookedDatesSection() {
    val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")

    val calendarDays: List<Int?> =
        List(3) { null } +
                (1..31).map { it } +
                listOf(null)

    CampsiteDetailCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = PrimaryColor
            )

            Text(
                text = "Currently Booked Dates",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "October 2026",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryColor
            )

            Text(
                text = "${bookedDates.size} booked dates",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Weekday headings
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            // Calendar weeks
            calendarDays.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        val isBooked = day != null && day in bookedDates

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .aspectRatio(1f)
                                .background(
                                    color = if (isBooked) {
                                        PrimaryColor
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = if (isBooked) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    },
                                    color = if (isBooked) {
                                        Color.White
                                    } else {
                                        PrimaryColor
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Calendar legend
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(
                        PrimaryColor,
                        RoundedCornerShape(2.dp)
                    )
            )

            Text(
                text = "Booked / reserved",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        HorizontalDivider(color = CardBorder)

        Text(
            text = "23–25 & 30–31 October are reserved.",
            fontSize = 11.sp,
            color = PrimaryColor
        )
    }
}

/**
 * Campsite occupancy, description, and tags.
 */
@Composable
private fun CampsiteDetailsSection() {
    CampsiteDetailCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Campsite Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ChipBackground
            ) {
                Text(
                    text = "Active",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 11.sp,
                    color = PrimaryColor
                )
            }
        }

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
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Column {
                Text(
                    text = "Maximum Occupancy",
                    fontSize = 11.sp,
                    color = SecondaryText
                )

                Text(
                    text = "10 people",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )
            }
        }

        HorizontalDivider(color = CardBorder)

        Text(
            text = "Amenities / Description",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Text(
            text = "Riverside campsite in Dartmoor with picnic tables, " +
                    "fire pits, and access to a nearby stream. Amenities " +
                    "include potable water, composting toilets, and parking " +
                    "for two vehicles per pitch.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = PrimaryColor
        )

        Text(
            text = "Tags",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampsiteTag("Riverside")
            CampsiteTag("Pet Friendly")
        }
    }
}

/**
 * Small label for campsite tags.
 */
@Composable
private fun CampsiteTag(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ChipBackground
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            fontSize = 10.sp,
            color = PrimaryColor
        )
    }
}

/**
 * Displays the campsite's rules and policies.
 */
@Composable
private fun CampsiteRulesSection() {
    CampsiteDetailCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = PrimaryColor
            )

            Text(
                text = "Rules & Policies",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
        }

        Text(
            text = "Campsite Rules",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Text(
            text = "Arrival is between 2 PM and 6 PM. Quiet hours are " +
                    "10 PM to 7 AM. Campfires are permitted in designated " +
                    "pits only. Please keep pets on a short lead and clean " +
                    "up after them. No amplified music.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = PrimaryColor
        )

        HorizontalDivider(color = CardBorder)

        Text(
            text = "Cancellation Policy",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Text(
            text = "Cancellations made more than 7 days before arrival " +
                    "receive a full refund. Cancellations made within " +
                    "7 days receive a 50% refund. No refunds are issued " +
                    "for same-day cancellations.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = PrimaryColor
        )

        HorizontalDivider(color = CardBorder)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Require entrant confirmation",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PrimaryColor
            ) {
                Text(
                    text = "ON",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }

        Text(
            text = "Entrants must confirm they have read these rules before reserving.",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = SecondaryText
        )
    }
}

/**
 * Links to lottery and reservation information.
 */
@Composable
private fun CampsiteViewSection(
    onLotteryClick: () -> Unit,
    onReservationsClick: () -> Unit
) {
    CampsiteDetailCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = PrimaryColor
            )

            Text(
                text = "View",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
        }

        // Lottery and waitlist details
        OwnerDetailLink(
            icon = Icons.Outlined.Groups,
            title = "Lottery & Waitlist Details",
            subtitle = "Entrants waiting for a place",
            count = "12",
            onClick = onLotteryClick
        )

        HorizontalDivider(color = CardBorder)

        // Reservations
        OwnerDetailLink(
            icon = Icons.Outlined.CalendarMonth,
            title = "Reservations",
            subtitle = "Review campsite reservations",
            onClick = onReservationsClick
        )
    }
}

/**
 * Clickable link displayed in the campsite detail card.
 */
@Composable
private fun OwnerDetailLink(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    count: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = PrimaryColor
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        if (count != null) {
            Surface(
                shape = CircleShape,
                color = ChipBackground
            ) {
                Text(
                    text = count,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 11.sp,
                    color = PrimaryColor
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = PrimaryColor
        )
    }
}
