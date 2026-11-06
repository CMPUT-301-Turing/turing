package com.turing.campsite.ui.mapper

import androidx.compose.ui.graphics.Color
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.ui.components.CampsiteFormData
import com.turing.campsite.ui.screens.entrant.EntrantCampsiteDetailsPreview
import java.util.Locale

/**
 * Placeholder colours used where a campsite picture will go later. The same
 * campsite always gets the same colour.
 */
private val placeholderColors = listOf(
    Color(0xFF82967A),
    Color(0xFF9CAB91),
    Color(0xFF8B9D91),
    Color(0xFF879F86)
)

/**
 * Picks the placeholder colour for a campsite.
 *
 * @param campsiteId the id of the campsite
 * @return one of the placeholder colours, always the same for the same id
 */
private fun colorFor(campsiteId: String): Color {
    val index = (campsiteId.hashCode() and Int.MAX_VALUE) % placeholderColors.size
    return placeholderColors[index]
}

/**
 * Turns a campsite from the database into the data for the entrant details screen.
 *
 * @return the details data for this campsite
 */
fun Campsite.toDetailsPreview(): EntrantCampsiteDetailsPreview {
    return EntrantCampsiteDetailsPreview(
        name = name,
        location = location,
        pitchCount = 0,
        type = tags.joinToString(" · "),
        peoplePerPitch = "",
        maximumOccupancy = "Max $maxOccupancy people",
        heading = "About this campsite",
        description = description,
        rating = String.format(Locale.US, "%.1f", ratingAverage),
        reviewCount = ratingCount,
        imageColor = colorFor(id)
    )
}

/**
 * Turns what the owner typed in the campsite form into a new campsite to save.
 *
 * A maximum occupancy that is not a number becomes 0.
 *
 * @param ownerId the uid of the owner saving the campsite
 * @return the campsite ready to be written to the database
 */
fun CampsiteFormData.toCampsite(ownerId: String): Campsite {
    return Campsite(
        ownerId = ownerId,
        name = name.trim(),
        location = location.trim(),
        description = description.trim(),
        maxOccupancy = maximumOccupancy.trim().toIntOrNull() ?: 0,
        tags = tags,
        rulesText = campsiteRules.trim(),
        mapLocation = mapLocation.trim(),
        cancellationPolicy = cancellationPolicy.trim(),
        confirmationRequired = confirmationRequired,
        active = isActive
    )
}
