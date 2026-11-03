package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable

import com.turing.campsite.ui.components.CampsiteForm
import com.turing.campsite.ui.components.CampsiteFormData
import com.turing.campsite.ui.components.CampsiteFormMode

/**
 * Temporary existing campsite data used to demonstrate editing.
 */
private val sampleCampsite = CampsiteFormData(
    name = "Riverbend Glade",
    maximumOccupancy = "10",
    description = "Riverside campsite in Dartmoor with picnic tables, fire pits, and access to a nearby stream. Amenities include potable water, composting toilets, and parking for two vehicles per pitch.",
    location = "Dartmoor, Devon",
    mapLocation = "Dartmoor, Devon",
    tags = listOf(
        "Riverside",
        "Pet Friendly"
    ),
    campsiteRules = "Arrival is between 2 PM and 6 PM. Quiet hours are 10 PM to 7 AM. Campfires are permitted in designated pits only. Please keep pets on a short lead and clean up after them. No amplified music.",
    cancellationPolicy = "Cancellations made more than 7 days before arrival receive a full refund. Cancellations made within 7 days receive a 50% refund. No refunds are issued for same-day cancellations.",
    confirmationRequired = true,
    isActive = true,
    hasPromoImage = true
)

/**
 * Screen for editing an existing campsite.
 *
 * Uses the shared campsite form populated with
 * existing data.
 *
 * Cancel discards changes by returning to the
 * previous screen. Update will eventually persist
 * the modified campsite information.
 */
@Composable
fun OwnerEditCampsiteScreen(
    onBackClick: () -> Unit,
    initialData: CampsiteFormData = sampleCampsite,
    onUpdateClick: (CampsiteFormData) -> Unit = {}
) {
    CampsiteForm(
        mode = CampsiteFormMode.EDIT,
        initialData = initialData,
        onBackClick = onBackClick,
        onSecondaryAction = {
            onBackClick()
        },
        onPrimaryAction = onUpdateClick
    )
}