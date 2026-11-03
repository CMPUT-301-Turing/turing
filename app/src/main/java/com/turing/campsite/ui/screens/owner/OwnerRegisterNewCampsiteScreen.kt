package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable

import com.turing.campsite.ui.components.CampsiteForm
import com.turing.campsite.ui.components.CampsiteFormData
import com.turing.campsite.ui.components.CampsiteFormMode

/**
 * Screen for registering a new campsite.
 *
 * Uses the shared campsite form with empty initial data.
 * Save as Draft and Publish will be connected to the
 * backend later.
 */
@Composable
fun OwnerRegisterNewCampsiteScreen(
    onBackClick: () -> Unit,
    onSaveDraftClick: (CampsiteFormData) -> Unit = {},
    onPublishClick: (CampsiteFormData) -> Unit = {}
) {
    CampsiteForm(
        mode = CampsiteFormMode.CREATE,
        initialData = CampsiteFormData(),
        onBackClick = onBackClick,
        onSecondaryAction = onSaveDraftClick,
        onPrimaryAction = onPublishClick
    )
}
