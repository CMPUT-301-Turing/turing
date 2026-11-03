package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val FieldBackground = Color(0xFFF4F2EB)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Determines the heading and actions shown by the form.
 */
enum class CampsiteFormMode {
    CREATE,
    EDIT
}

/**
 * Editable data for campsite registration and editing.
 *
 * This is temporary UI data, not the Firebase model.
 */
data class CampsiteFormData(
    val name: String = "",
    val maximumOccupancy: String = "",
    val description: String = "",
    val location: String = "",
    val mapLocation: String = "",
    val tags: List<String> = emptyList(),
    val campsiteRules: String = "",
    val cancellationPolicy: String = "",
    val confirmationRequired: Boolean = true,
    val isActive: Boolean = true,
    val hasPromoImage: Boolean = false
)

/**
 * Shared form for registering and editing campsites.
 *
 * Contains local state and forwards actions to the parent
 * screen. Persistence is not implemented here.
 */
@Composable
fun CampsiteForm(
    mode: CampsiteFormMode,
    onBackClick: () -> Unit,
    onPrimaryAction: (CampsiteFormData) -> Unit,
    onSecondaryAction: (CampsiteFormData) -> Unit,
    initialData: CampsiteFormData = CampsiteFormData()
) {
    var formData by remember(initialData) {
        mutableStateOf(initialData)
    }

    val isEditing = mode == CampsiteFormMode.EDIT

    val title = if (isEditing) {
        "Edit Campsite"
    } else {
        "Campsite Registration"
    }

    val description = if (isEditing) {
        "Review and update the details for ${formData.name}."
    } else {
        "Create a welcoming place to stay. Add your campsite details below."
    }

    val primaryLabel = if (isEditing) "Update" else "Publish"
    val secondaryLabel = if (isEditing) "Cancel" else "Save as Draft"

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

        // Scrollable form
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

            CampsiteImageSection(
                hasImage = formData.hasPromoImage
            )

            CampsiteBasicInfoSection(
                data = formData,
                onDataChange = { formData = it }
            )

            CampsiteLocationSection(
                data = formData,
                onDataChange = { formData = it }
            )

            CampsiteRulesSection(
                data = formData,
                onDataChange = { formData = it }
            )

            CampsiteAvailabilitySection(
                isActive = formData.isActive,
                isEditing = isEditing,
                onActiveChange = {
                    formData = formData.copy(isActive = it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Fixed bottom action buttons
        HorizontalDivider(color = CardBorder)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    onSecondaryAction(formData)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CardBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryColor
                )
            ) {
                Text(
                    text = secondaryLabel,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    onPrimaryAction(formData)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                )
            ) {
                Text(
                    text = primaryLabel,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Shared card layout for each form section.
 */
@Composable
private fun CampsiteFormSection(
    title: String,
    description: String? = null,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            if (description != null) {
                Text(
                    text = description,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = SecondaryText
                )
            }

            content()
        }
    }
}

/**
 * Image area.
 *
 * Image upload and preview will be connected later.
 * For now, the edit screen shows a visual placeholder.
 */
@Composable
private fun CampsiteImageSection(
    hasImage: Boolean
) {
    if (!hasImage) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .drawBehind {
                    drawRoundRect(
                        color = CardBorder,
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(
                                    7.dp.toPx(),
                                    5.dp.toPx()
                                )
                            )
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddAPhoto,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = PrimaryColor
                )

                Text(
                    text = "Upload Promo Image (Optional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )

                Text(
                    text = "JPG or PNG · Up to 10 MB",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF789578),
                            Color(0xFF41664D)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            // Placeholder until actual campsite images are loaded
            Icon(
                imageVector = Icons.Outlined.Terrain,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                tint = Color.White
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(7.dp),
                color = CardBackground
            ) {
                Text(
                    text = "Change Image",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    fontSize = 11.sp,
                    color = PrimaryColor
                )
            }
        }
    }
}

/**
 * Basic campsite information.
 */
@Composable
private fun CampsiteBasicInfoSection(
    data: CampsiteFormData,
    onDataChange: (CampsiteFormData) -> Unit
) {
    CampsiteFormSection(title = "Basic Info") {
        CampsiteTextField(
            label = "Campsite Name",
            value = data.name,
            placeholder = "Enter campsite name",
            onValueChange = {
                onDataChange(data.copy(name = it))
            }
        )

        CampsiteTextField(
            label = "Maximum Occupancy",
            value = data.maximumOccupancy,
            placeholder = "e.g. 8",
            trailingText = "people",
            numeric = true,
            onValueChange = {
                onDataChange(
                    data.copy(maximumOccupancy = it)
                )
            }
        )

        CampsiteTextField(
            label = "Description (Amenities)",
            value = data.description,
            placeholder = "Describe your campsite, facilities and amenities...",
            minLines = 4,
            onValueChange = {
                onDataChange(data.copy(description = it))
            }
        )
    }
}

/**
 * Location, map placeholder, and editable tags.
 */
@Composable
private fun CampsiteLocationSection(
    data: CampsiteFormData,
    onDataChange: (CampsiteFormData) -> Unit
) {
    var tagInput by remember {
        mutableStateOf("")
    }

    fun addTag() {
        val newTag = tagInput.trim()

        if (
            newTag.isNotEmpty() &&
            data.tags.none { it.equals(newTag, ignoreCase = true) }
        ) {
            onDataChange(
                data.copy(tags = data.tags + newTag)
            )
        }

        tagInput = ""
    }

    CampsiteFormSection(title = "Location & Tags") {
        CampsiteTextField(
            label = "Location",
            value = data.location,
            placeholder = "Town, region or address",
            onValueChange = {
                onDataChange(data.copy(location = it))
            }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Map Location",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryColor
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(7.dp),
                color = FieldBackground,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        tint = PrimaryColor
                    )

                    Text(
                        text = data.mapLocation.ifBlank {
                            "No pin set"
                        },
                        modifier = Modifier.weight(1f),
                        fontSize = 11.sp,
                        color = SecondaryText
                    )

                    // Map selection placeholder
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ChipBackground
                    ) {
                        Text(
                            text = "Select on Map",
                            modifier = Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 8.dp
                            ),
                            fontSize = 11.sp,
                            color = PrimaryColor
                        )
                    }
                }
            }
        }

        CampsiteTextField(
            label = "Tags",
            value = tagInput,
            placeholder = if (data.tags.isEmpty()) {
                "Type a tag and press enter"
            } else {
                "Add another tag"
            },
            onValueChange = { tagInput = it },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { addTag() }
            )
        )

        // Current tags (tap one to remove it)
        if (data.tags.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                data.tags.forEach { tag ->
                    Surface(
                        onClick = {
                            onDataChange(
                                data.copy(
                                    tags = data.tags - tag
                                )
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = ChipBackground
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = PrimaryColor
                            )

                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Remove $tag",
                                modifier = Modifier.size(13.dp),
                                tint = PrimaryColor
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Add tags to help campers find your site.",
            fontSize = 10.sp,
            color = SecondaryText
        )
    }
}

/**
 * Campsite rules, cancellation policy, and confirmation setting.
 */
@Composable
private fun CampsiteRulesSection(
    data: CampsiteFormData,
    onDataChange: (CampsiteFormData) -> Unit
) {
    CampsiteFormSection(
        title = "Rules & Policies",
        description = "Set clear expectations before campers reserve."
    ) {
        CampsiteTextField(
            label = "Campsite Rules",
            value = data.campsiteRules,
            placeholder = "Add arrival times, quiet hours, fire safety and other campsite rules...",
            minLines = 4,
            onValueChange = {
                onDataChange(data.copy(campsiteRules = it))
            }
        )

        CampsiteTextField(
            label = "Cancellation Policy",
            value = data.cancellationPolicy,
            placeholder = "Explain cancellation deadlines, refunds and any exceptions...",
            minLines = 4,
            onValueChange = {
                onDataChange(data.copy(cancellationPolicy = it))
            }
        )

        HorizontalDivider(color = CardBorder)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Require entrants to confirm they have read these rules before reserving.",
                modifier = Modifier.weight(1f),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = PrimaryColor
            )

            Switch(
                checked = data.confirmationRequired,
                onCheckedChange = {
                    onDataChange(
                        data.copy(confirmationRequired = it)
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = PrimaryColor,
                    checkedThumbColor = Color.White
                )
            )
        }
    }
}

/**
 * Allows an owner to mark a campsite active or inactive.
 */
@Composable
private fun CampsiteAvailabilitySection(
    isActive: Boolean,
    isEditing: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    CampsiteFormSection(
        title = "Availability",
        description = "Choose whether this campsite can accept reservations."
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ChipBackground,
                    RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(true, false).forEach { activeOption ->
                val selected = isActive == activeOption

                Surface(
                    onClick = {
                        onActiveChange(activeOption)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    color = if (selected) {
                        PrimaryColor
                    } else {
                        ChipBackground
                    }
                ) {
                    Text(
                        text = if (activeOption) {
                            "✓  Active"
                        } else {
                            "Inactive"
                        },
                        modifier = Modifier.padding(
                            vertical = 12.dp
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (selected) {
                            Color.White
                        } else {
                            PrimaryColor
                        }
                    )
                }
            }
        }

        Text(
            text = if (isEditing) {
                "Select Update to save your changes."
            } else {
                "Drafts stay private, regardless of availability."
            },
            fontSize = 10.sp,
            color = SecondaryText
        )
    }
}

/**
 * Shared text input with optional multiline or numeric behavior.
 */
@Composable
private fun CampsiteTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    trailingText: String? = null,
    numeric: Boolean = false,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
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

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (!numeric || newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = SecondaryText
                )
            },
            trailingIcon = if (trailingText != null) {
                {
                    Text(
                        text = trailingText,
                        modifier = Modifier.padding(end = 10.dp),
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                }
            } else {
                null
            },
            singleLine = minLines == 1,
            minLines = minLines,
            maxLines = if (minLines == 1) 1 else 6,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = PrimaryColor
            ),
            keyboardOptions = if (numeric) {
                KeyboardOptions(keyboardType = KeyboardType.Number)
            } else {
                keyboardOptions
            },
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = CardBorder,
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground
            )
        )
    }
}
