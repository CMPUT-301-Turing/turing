
package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable
import com.turing.campsite.data.model.Campsite

import com.turing.campsite.ui.components.LotteryForm
import com.turing.campsite.ui.components.LotteryFormData
import com.turing.campsite.ui.components.LotteryFormMode
import com.turing.campsite.ui.viewmodel.LotteryViewModel

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
    lotteryViewModel: LotteryViewModel,
    campsites: List<Campsite> = emptyList(),
    onBackClick: () -> Unit,
    initialData: LotteryFormData = LotteryFormData(),
    onUpdateClick: (LotteryFormData) -> Unit = {}
) {
    LotteryForm(
        lotteryViewModel = lotteryViewModel,
        mode = LotteryFormMode.EDIT,
        campsites = campsites,
        title = "Edit Lottery",
        description = "Review your existing lottery. Update the campsites, dates, winner count or waitlist capacity.",
        actionLabel = "Update Lottery",
        onBackClick = onBackClick,
        onSubmitClick = onUpdateClick,
        initialData = initialData
    )
}
