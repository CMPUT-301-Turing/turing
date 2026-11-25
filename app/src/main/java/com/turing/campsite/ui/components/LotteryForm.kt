package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val FieldBackground = Color(0xFFF4F2EB)
private val SelectedBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Temporary campsite information for lottery selection.
 */
data class LotteryCampsitePreview(
    val id: String,
    val name: String,
    val location: String
)

/**
 * Form data for creating or editing a lottery.
 *
 * Values are temporary until database integration.
 */
data class LotteryFormData(
    val selectedCampsiteIds: Set<String> = emptySet(),
    val registrationStart: String = "",
    val registrationEnd: String = "",
    val bookingStart: String = "",
    val bookingEnd: String = "",
    val numberOfWinners: String = "",
    val maximumCapacityEnabled: Boolean = false,
    val waitlistCapacity: String = ""
)

private val sampleCampsites = listOf(
    LotteryCampsitePreview(
        id = "pinewood",
        name = "Pinewood Retreat",
        location = "New Forest, Hampshire"
    ),
    LotteryCampsitePreview(
        id = "willowbank",
        name = "Willowbank Meadow",
        location = "Bakewell, Peak District"
    ),
    LotteryCampsitePreview(
        id = "fellside",
        name = "Fellside Farm",
        location = "Keswick, Lake District"
    ),
    LotteryCampsitePreview(
        id = "riverbend",
        name = "Riverbend Glade",
        location = "Dartmoor, Devon"
    )
)

/**
 * Shared form for creating and editing lotteries.
 *
 * The parent screen supplies the title, description,
 * action label and navigation callbacks.
 *
 * Form values are stored locally for now.
 */
@Composable
fun LotteryForm(
    title: String,
    description: String,
    actionLabel: String,
    onBackClick: () -> Unit,
    onSubmitClick: (LotteryFormData) -> Unit,
    initialData: LotteryFormData = LotteryFormData()
) {
    var formData by remember(initialData) {
        mutableStateOf(initialData)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 20.dp,
                    top = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        // Scrollable form content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = SecondaryText
            )

            LotteryCampsiteSection(
                campsites = sampleCampsites,
                selectedIds = formData.selectedCampsiteIds,
                onCampsiteToggled = { campsiteId ->
                    val selectedIds = formData.selectedCampsiteIds

                    formData = formData.copy(
                        selectedCampsiteIds = if (campsiteId in selectedIds) {
                            selectedIds - campsiteId
                        } else {
                            selectedIds + campsiteId
                        }
                    )
                }
            )

            LotteryDatePeriodSection(
                title = "Registration Period",
                description = "When entrants can join the waitlist.",
                startDate = formData.registrationStart,
                endDate = formData.registrationEnd,
                onStartDateChange = {
                    formData = formData.copy(
                        registrationStart = it
                    )
                },
                onEndDateChange = {
                    formData = formData.copy(
                        registrationEnd = it
                    )
                }
            )

            LotteryDatePeriodSection(
                title = "Booking Period",
                description = "When winners can book their stay.",
                startDate = formData.bookingStart,
                endDate = formData.bookingEnd,
                onStartDateChange = {
                    formData = formData.copy(
                        bookingStart = it
                    )
                },
                onEndDateChange = {
                    formData = formData.copy(
                        bookingEnd = it
                    )
                }
            )

            LotterySettingsSection(
                numberOfWinners = formData.numberOfWinners,
                maximumCapacityEnabled = formData.maximumCapacityEnabled,
                waitlistCapacity = formData.waitlistCapacity,
                onNumberOfWinnersChange = {
                    formData = formData.copy(
                        numberOfWinners = it
                    )
                },
                onMaximumCapacityChange = {
                    formData = formData.copy(
                        maximumCapacityEnabled = it
                    )
                },
                onWaitlistCapacityChange = {
                    formData = formData.copy(
                        waitlistCapacity = it
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Persistent bottom action
        HorizontalDivider(color = CardBorder)

        Button(
            onClick = {
                onSubmitClick(formData)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 14.dp
                )
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            )
        ) {
            Text(
                text = actionLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

/**
 * Common card styling for lottery form sections.
 */
@Composable
private fun LotterySectionCard(
    title: String,
    description: String,
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = description,
                fontSize = 11.sp,
                color = SecondaryText
            )

            content()
        }
    }
}

/**
 * Lets the owner select one or more campsites.
 */
@Composable
private fun LotteryCampsiteSection(
    campsites: List<LotteryCampsitePreview>,
    selectedIds: Set<String>,
    onCampsiteToggled: (String) -> Unit
) {
    LotterySectionCard(
        title = "Select Campsites",
        description = "Choose one or more campsites you own."
    ) {
        campsites.forEach { campsite ->
            val isSelected = campsite.id in selectedIds

            Surface(
                onClick = {
                    onCampsiteToggled(campsite.id)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) {
                    SelectedBackground
                } else {
                    PageBackground
                },
                border = BorderStroke(
                    1.dp,
                    if (isSelected) PrimaryColor else CardBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            onCampsiteToggled(campsite.id)
                        },
                        modifier = Modifier.size(24.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryColor,
                            uncheckedColor = SecondaryText,
                            checkmarkColor = Color.White
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = campsite.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )

                        Text(
                            text = campsite.location,
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                }
            }
        }

        Text(
            text = "${selectedIds.size} of ${campsites.size} campsites selected",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )
    }
}

/**
 * Displays a start and end date for a lottery period.
 *
 * The same component is used for registration and booking.
 */
@Composable
private fun LotteryDatePeriodSection(
    title: String,
    description: String,
    startDate: String,
    endDate: String,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit
) {
    LotterySectionCard(
        title = title,
        description = description
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LotteryDateInput(
                label = "Start Date",
                value = startDate,
                onValueChange = onStartDateChange,
                modifier = Modifier.weight(1f)
            )

            LotteryDateInput(
                label = "End Date",
                value = endDate,
                onValueChange = onEndDateChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Editable date field with a calendar icon.
 *
 * A native date picker can replace this text input
 * when date validation is implemented.
 */
@Composable
private fun LotteryDateInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(7.dp),
            color = FieldBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = PrimaryColor
                    ),
                    cursorBrush = SolidColor(PrimaryColor)
                )

                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = PrimaryColor
                )
            }
        }
    }
}

/**
 * Lottery winner count and optional waitlist capacity.
 */
@Composable
private fun LotterySettingsSection(
    numberOfWinners: String,
    maximumCapacityEnabled: Boolean,
    waitlistCapacity: String,
    onNumberOfWinnersChange: (String) -> Unit,
    onMaximumCapacityChange: (Boolean) -> Unit,
    onWaitlistCapacityChange: (String) -> Unit
) {
    LotterySectionCard(
        title = "Lottery Settings",
        description = ""
    ) {
        LotteryNumberInput(
            label = "Number of Winners",
            value = numberOfWinners,
            suffix = "winners",
            onValueChange = onNumberOfWinnersChange
        )

        Text(
            text = "Total winners across the selected campsites.",
            fontSize = 11.sp,
            color = SecondaryText
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 6.dp),
            color = CardBorder
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Set maximum capacity",
                fontSize = 12.sp,
                color = PrimaryColor
            )

            Switch(
                checked = maximumCapacityEnabled,
                onCheckedChange = onMaximumCapacityChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryColor
                )
            )
        }

        if (maximumCapacityEnabled) {
            LotteryNumberInput(
                label = "Waitlist Capacity",
                value = waitlistCapacity,
                suffix = "entrants",
                onValueChange = onWaitlistCapacityChange
            )
        }

        Text(
            text = "Registration closes when the waitlist is full. Turn off for unlimited entries.",
            fontSize = 10.sp,
            lineHeight = 14.sp,
            color = SecondaryText
        )
    }
}

/**
 * Editable numeric input with a unit label.
 */
@Composable
private fun LotteryNumberInput(
    label: String,
    value: String,
    suffix: String,
    onValueChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(7.dp),
            color = FieldBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            onValueChange(newValue)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        color = PrimaryColor
                    ),
                    cursorBrush = SolidColor(PrimaryColor)
                )

                Text(
                    text = suffix,
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }
    }
}
