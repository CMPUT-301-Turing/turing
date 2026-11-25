
package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable
import com.turing.campsite.data.auth.DeviceAuthManager

import com.turing.campsite.ui.components.LotteryForm
import com.turing.campsite.ui.components.LotteryFormData
import com.turing.campsite.ui.viewmodel.CampsiteViewModel

/**
 * Screen for editing an existing campsite lottery.
 *
 * Uses the shared lottery form, initialized with
 * existing lottery values when available.
 *
 * Updating the lottery will be implemented later.
 */
@Composable
fun OwnerEditLotteryScreen(
    campsiteViewModel: CampsiteViewModel,
    deviceAuthManager: DeviceAuthManager,
    onBackClick: () -> Unit,
    initialData: LotteryFormData = LotteryFormData(),
    onUpdateClick: (LotteryFormData) -> Unit = {}
) {
    LotteryForm(
        campsiteViewModel,
        deviceAuthManager,
        title = "Edit Lottery",
        description = "Review your existing lottery. Update the campsites, dates, winner count or waitlist capacity.",
        actionLabel = "Update Lottery",
        onBackClick = onBackClick,
        onSubmitClick = onUpdateClick,
        initialData = initialData
    )
}
