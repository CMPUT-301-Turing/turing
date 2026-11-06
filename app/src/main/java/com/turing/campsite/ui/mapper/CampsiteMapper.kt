package com.turing.campsite.ui.mapper

import com.turing.campsite.data.model.Campsite
import com.turing.campsite.ui.components.CampsiteFormData

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
