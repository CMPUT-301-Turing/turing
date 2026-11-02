package com.turing.campsite.ui.screens.entrant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

import com.turing.campsite.ui.components.ReservationCard
import com.turing.campsite.ui.components.ReservationPreview
import com.turing.campsite.ui.components.ReservationStatus
import com.turing.campsite.ui.components.LotteryEntryCard
import com.turing.campsite.ui.components.LotteryEntryPreview
import com.turing.campsite.ui.components.LotteryEntryStatus
import com.turing.campsite.ui.components.LotteryResultCard
import com.turing.campsite.ui.components.LotteryResultPreview
import com.turing.campsite.ui.components.LotteryResultStatus

private val PageBackground = Color(0xFFF6F4ED)
private val DropdownBackground = Color(0xFFE8EFE3)
private val CardBackground = Color(0xFFFCFBF7)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Available views within the entrant campsite lists screen.
 */
private enum class CampsiteListSection(val label: String) {
    ACTIVE_RESERVATIONS("Active reservations"),
    INACTIVE_RESERVATIONS("Inactive reservations"),
    WAITLISTS_AND_LOTTERIES("Waitlists & lotteries"),
    LOTTERY_RESULTS("Lottery results")
}

/**
 * Temporary active reservation data.
 */
private val activeReservations = listOf(
    ReservationPreview(
        reservationId = "WM-1041",
        campsiteName = "Willowbank Meadow",
        location = "Bakewell, Peak District",
        dateRange = "13–15 Nov",
        nights = 2,
        adults = 2,
        tents = 1,
        environment = "Riverside",
        status = ReservationStatus.CONFIRMED,
        imageColor = Color(0xFF9CAB91)
    ),
    ReservationPreview(
        reservationId = "BH-1043",
        campsiteName = "Bracken Hollow",
        location = "Lyndhurst, Hampshire",
        dateRange = "27–29 Nov",
        nights = 2,
        adults = 2,
        tents = 1,
        environment = "Woodland",
        status = ReservationStatus.CONFIRMED,
        imageColor = Color(0xFF82967A)
    )
)

/**
 * Temporary inactive reservation data.
 */
private val inactiveReservations = listOf(
    ReservationPreview(
        reservationId = "HV-0986",
        campsiteName = "Hawthorn Vale",
        location = "Keswick, Lake District",
        dateRange = "9–11 Oct",
        nights = 2,
        adults = 2,
        tents = 1,
        environment = "Lakeside",
        status = ReservationStatus.PAST,
        imageColor = Color(0xFF9D9B7B)
    ),
    ReservationPreview(
        reservationId = "PR-1042",
        campsiteName = "Pinewood Retreat",
        location = "New Forest, Hampshire",
        dateRange = "6–8 Nov",
        nights = 2,
        adults = 2,
        tents = 1,
        environment = "Woodland",
        status = ReservationStatus.CANCELLED,
        imageColor = Color(0xFF82967A)
    )
)


/**
 * Temporary lottery entry data.
 */
private val currentLotteryEntries = listOf(
    LotteryEntryPreview(
        id = "lottery-pinewood",
        campsiteName = "Pinewood Retreat",
        location = "New Forest, Hampshire",
        dateRange = "20–22 Nov",
        nights = 2,
        note = "Entries close 26 Oct",
        status = LotteryEntryStatus.ENTERED,
        imageColor = Color(0xFF82967A)
    )
)

/**
 * Temporary lottery waitlist data.
 */
private val lotteryWaitlistEntries = listOf(
    LotteryEntryPreview(
        id = "waitlist-oakridge",
        campsiteName = "Oakridge Farm",
        location = "Ashbourne, Peak District",
        dateRange = "20–22 Nov",
        nights = 2,
        note = "We'll notify you if a pitch opens.",
        status = LotteryEntryStatus.WAITLISTED,
        imageColor = Color(0xFF9D9B7B)
    )
)


/**
 * Temporary lottery results.
 */
private val lotteryResults = listOf(
    LotteryResultPreview(
        id = "result-willowbank",
        campsiteName = "Willowbank Meadow",
        status = LotteryResultStatus.WON,
        dateRange = "13–15 Nov · 2 nights",
        message = "A pitch is yours to book.",
        timestamp = "Announced today, 09:12",
        deadline = "Accept your place by 21 Oct",
        imageColor = Color(0xFF9CAB91)
    ),
    LotteryResultPreview(
        id = "result-pinewood",
        campsiteName = "Pinewood Retreat",
        status = LotteryResultStatus.PENDING,
        message = "Lottery open. Your entry is in the draw.",
        timestamp = "Entries close 26 Oct"
    ),
    LotteryResultPreview(
        id = "result-fellside",
        campsiteName = "Fellside Farm",
        status = LotteryResultStatus.LOST,
        dateRange = "6–8 Nov",
        message = "You weren't selected this time. Try a future draw.",
        timestamp = "Announced today, 08:45"
    )
)


/**
 * Entrant campsite lists screen.
 *
 * Provides a shared dropdown for reservations and lotteries.
 * The selected section is preserved through supported
 * navigation state restoration.
 */
@Composable
fun EntrantCampsiteListScreen() {
    var selectedSection by rememberSaveable {
        mutableStateOf(CampsiteListSection.ACTIVE_RESERVATIONS)
    }

    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    val sectionCount = when (selectedSection) {
        CampsiteListSection.ACTIVE_RESERVATIONS ->
            activeReservations.size

        CampsiteListSection.INACTIVE_RESERVATIONS ->
            inactiveReservations.size

        CampsiteListSection.WAITLISTS_AND_LOTTERIES ->
            currentLotteryEntries.size + lotteryWaitlistEntries.size

        CampsiteListSection.LOTTERY_RESULTS ->
            lotteryResults.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Text(
            text = "Campsite lists",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Your next little escapes, all in one place.",
            fontSize = 12.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(24.dp))

        // List selector
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DropdownBackground)
                    .clickable { dropdownExpanded = true }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedSection.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor,
                    modifier = Modifier.weight(1f)
                )

                // Reservation count
                if (sectionCount != null) {
                    Surface(
                        shape = CircleShape,
                        color = CardBackground
                    ) {
                        Text(
                            text = sectionCount.toString(),
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            fontSize = 11.sp,
                            color = PrimaryColor
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select campsite list",
                    modifier = Modifier.size(20.dp),
                    tint = PrimaryColor
                )
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                CampsiteListSection.entries.forEach { section ->
                    DropdownMenuItem(
                        text = { Text(section.label) },
                        onClick = {
                            selectedSection = section
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Selected content
        when (selectedSection) {
            CampsiteListSection.ACTIVE_RESERVATIONS ->
                ActiveReservationsContent()

            CampsiteListSection.INACTIVE_RESERVATIONS ->
                InactiveReservationsContent()

            CampsiteListSection.WAITLISTS_AND_LOTTERIES ->
                WaitlistsAndLotteriesContent()

            CampsiteListSection.LOTTERY_RESULTS ->
                LotteryResultsContent()
        }
    }
}

/**
 * Displays active reservations.
 */
@Composable
private fun ActiveReservationsContent() {
    ReservationListContent(activeReservations)
}

/**
 * Displays inactive reservations.
 */
@Composable
private fun InactiveReservationsContent() {
    ReservationListContent(inactiveReservations)
}

/**
 * Shared list implementation for reservation cards.
 */
@Composable
private fun ReservationListContent(
    reservations: List<ReservationPreview>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(
            items = reservations,
            key = { it.reservationId }
        ) { reservation ->
            ReservationCard(reservation)
        }
    }
}

/**
 * Placeholder: waitlists and lotteries.
 */

/**
 * Displays current lottery entries and lottery waitlist entries.
 */
@Composable
private fun WaitlistsAndLotteriesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        // Current lottery entries
        item {
            LotterySectionHeader(
                title = "Current lottery entries",
                count = currentLotteryEntries.size
            )
        }

        items(
            items = currentLotteryEntries,
            key = { it.id }
        ) { entry ->
            LotteryEntryCard(entry)
        }

        // Lottery waitlist entries
        item {
            Spacer(modifier = Modifier.height(4.dp))

            LotterySectionHeader(
                title = "Lottery waitlist entries",
                count = lotteryWaitlistEntries.size
            )
        }

        items(
            items = lotteryWaitlistEntries,
            key = { it.id }
        ) { entry ->
            LotteryEntryCard(entry)
        }
    }
}

/**
 * Displays a lottery section heading and entry count.
 */
@Composable
private fun LotterySectionHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Text(
            text = if (count == 1) "1 entry" else "$count entries",
            fontSize = 11.sp,
            color = SecondaryText
        )
    }
}



/**
 * Displays winning, pending, and unsuccessful lottery results.
 */
@Composable
private fun LotteryResultsContent() {
    val winningResults = lotteryResults.filter {
        it.status == LotteryResultStatus.WON
    }

    val otherResults = lotteryResults.filter {
        it.status != LotteryResultStatus.WON
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        // Winning lottery results
        items(
            items = winningResults,
            key = { it.id }
        ) { result ->
            LotteryResultCard(result)
        }

        // Other lottery entries
        if (otherResults.isNotEmpty()) {
            item {
                Text(
                    text = "Your other entries",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )
            }

            items(
                items = otherResults,
                key = { it.id }
            ) { result ->
                LotteryResultCard(result)
            }
        }
    }
}