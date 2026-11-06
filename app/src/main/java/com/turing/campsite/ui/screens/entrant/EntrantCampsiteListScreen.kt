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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.auth.FirebaseAuth
import com.turing.campsite.PrimaryColor

import com.turing.campsite.ui.components.ReservationCard
import com.turing.campsite.data.model.Reservation
import com.turing.campsite.data.model.ReservationStatus
import com.turing.campsite.ui.components.LotteryEntryCard
import com.turing.campsite.data.model.LotteryEntry
import com.turing.campsite.data.model.EntryResult
import com.turing.campsite.ui.components.LotteryResultCard
import com.turing.campsite.ui.viewmodel.ReservationViewModel

private val PageBackground = Color(0xFFF6F4ED)
private val DropdownBackground = Color(0xFFE8EFE3)
private val CardBackground = Color(0xFFFCFBF7)
private val SecondaryText = Color(0xFF7A8079)

private enum class CampsiteListSection(val label: String) {
    ACTIVE_RESERVATIONS("Active reservations"),
    INACTIVE_RESERVATIONS("Inactive reservations"),
    WAITLISTS_AND_LOTTERIES("Waitlists & lotteries"),
    LOTTERY_RESULTS("Lottery results")
}

private val currentLotteryEntries = listOf(
    LotteryEntry(id = "Pinewood Retreat", result = EntryResult.PENDING)
)

private val lotteryWaitlistEntries = listOf(
    LotteryEntry(id = "Oakridge Farm", result = EntryResult.PENDING)
)

private val lotteryResults = listOf(
    LotteryEntry(id = "Willowbank Meadow", result = EntryResult.WON),
    LotteryEntry(id = "Pinewood Retreat", result = EntryResult.PENDING),
    LotteryEntry(id = "Fellside Farm", result = EntryResult.LOST)
)

@Composable
fun EntrantCampsiteListScreen(
    reservationViewModel: ReservationViewModel
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val allReservations = reservationViewModel.reservations

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            reservationViewModel.loadForEntrant(currentUserId)
        }
    }

    val activeReservations = allReservations.filter { it.status == ReservationStatus.ACTIVE }
    val inactiveReservations = allReservations.filter {
        it.status == ReservationStatus.COMPLETED || it.status == ReservationStatus.CANCELLED || it.status == ReservationStatus.REJECTED
    }

    var selectedSection by rememberSaveable {
        mutableStateOf(CampsiteListSection.ACTIVE_RESERVATIONS)
    }

    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    val sectionCount = when (selectedSection) {
        CampsiteListSection.ACTIVE_RESERVATIONS -> activeReservations.size
        CampsiteListSection.INACTIVE_RESERVATIONS -> inactiveReservations.size
        CampsiteListSection.WAITLISTS_AND_LOTTERIES -> currentLotteryEntries.size + lotteryWaitlistEntries.size
        CampsiteListSection.LOTTERY_RESULTS -> lotteryResults.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
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

        when (selectedSection) {
            CampsiteListSection.ACTIVE_RESERVATIONS -> ReservationListContent(activeReservations)
            CampsiteListSection.INACTIVE_RESERVATIONS -> ReservationListContent(inactiveReservations)
            CampsiteListSection.WAITLISTS_AND_LOTTERIES -> WaitlistsAndLotteriesContent()
            CampsiteListSection.LOTTERY_RESULTS -> LotteryResultsContent()
        }
    }
}

@Composable
private fun ReservationListContent(
    reservations: List<Reservation>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(
            items = reservations,
            key = { it.id }
        ) { reservation ->
            ReservationCard(reservation)
        }
    }
}

@Composable
private fun WaitlistsAndLotteriesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
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

@Composable
private fun LotteryResultsContent() {
    val winningResults = lotteryResults.filter {
        it.result == EntryResult.WON
    }

    val otherResults = lotteryResults.filter {
        it.result != EntryResult.WON
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        items(
            items = winningResults,
            key = { it.id }
        ) { result ->
            LotteryResultCard(result)
        }

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