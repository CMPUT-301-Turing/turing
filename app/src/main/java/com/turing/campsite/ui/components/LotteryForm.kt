package com.turing.campsite.ui.components

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.ui.viewmodel.LotteryViewModel
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.Date

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val FieldBackground = Color(0xFFF4F2EB)
private val SelectedBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)


/**
 * Form data for creating or editing a lottery.
 */
data class LotteryFormData(
    val selectedCampsiteIds: Set<String> = emptySet(),
    val registrationStart: Timestamp? = null,
    val registrationEnd: Timestamp? = null,
    val bookingStart: Timestamp? = null,
    val bookingEnd: Timestamp? = null,
    val numberOfWinners: String = "",
    val maximumCapacityEnabled: Boolean = false,
    val lotteryCapacity: String = "",
    val ownersList: List<String> = emptyList(),
    val name: String = ""
)

enum class LotteryFormMode {
    CREATE,
    EDIT,
    MULTI
}

/**
 * Shared form for creating and editing lotteries.
 *
 * The parent screen supplies the title, description,
 * action label and navigation callbacks.
 *
 * Form values are stored locally for now. [campsites] are the owner's
 * campsites the lottery can include.
 */
@Composable
fun LotteryForm(
    lotteryViewModel: LotteryViewModel,
    mode: LotteryFormMode,
    campsites: List<Campsite> = emptyList(),
    title: String,
    description: String,
    actionLabel: String,
    onBackClick: () -> Unit,
    onSubmitClick: (LotteryFormData) -> Unit,
    initialData: LotteryFormData = LotteryFormData(),
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
                campsites = campsites,
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
                onStartDateChange= {
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
                lotteryCapacity = formData.lotteryCapacity,
                name = formData.name,
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
                onLotteryCapacityChange = {
                    formData = formData.copy(
                        lotteryCapacity = it
                    )
                },
                onNameChange = {
                    formData = formData.copy(
                        name = it
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
    campsites: List<Campsite>,
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
    startDate: Timestamp?,
    endDate: Timestamp?,
    onStartDateChange: (Timestamp) -> Unit,
    onEndDateChange: (Timestamp) -> Unit
) {
    LotterySectionCard(
        title = title,
        description = description
    ) {
        val state = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate?.toDate()?.time,
            initialSelectedEndDateMillis = endDate?.toDate()?.time
        )
        LaunchedEffect(state.selectedStartDateMillis) {
            state.selectedStartDateMillis?.let { millis ->
                onStartDateChange(Timestamp(Date(millis)))
            }
        }
        LaunchedEffect(state.selectedEndDateMillis) {
            state.selectedEndDateMillis?.let { millis ->
                onEndDateChange(Timestamp(Date(millis)))
            }
        }



            DateRangePicker(state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp))
        }
    }


/**
 * Lottery winner count and optional waitlist capacity.
 */
@Composable
private fun LotterySettingsSection(
    numberOfWinners: String,
    maximumCapacityEnabled: Boolean,
    lotteryCapacity: String,
    name: String,
    onNumberOfWinnersChange: (String) -> Unit,
    onMaximumCapacityChange: (Boolean) -> Unit,
    onLotteryCapacityChange: (String) -> Unit,
    onNameChange: (String) -> Unit
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
                label = "Lottery Capacity",
                value = lotteryCapacity,
                suffix = "entrants",
                onValueChange = onLotteryCapacityChange
            )
        }

        Text(
            text = "Additional entrants added to a waitlist",
            fontSize = 10.sp,
            lineHeight = 14.sp,
            color = SecondaryText
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Name Your Lottery",
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
                        value = name,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isLetter() }) {
                                onNameChange(newValue)
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
                        cursorBrush = SolidColor(PrimaryColor),
                        decorationBox = {innerTextField ->
                            if (name.isEmpty()){
                                Text(
                                    text = "Enter a name",
                                    color = SecondaryText,
                                    fontSize = 12.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                }
            }
        }
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
