
package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable

import com.turing.campsite.ui.components.LotteryForm
import com.turing.campsite.ui.components.LotteryFormData

/**
 * Screen for creating a new campsite lottery.
 *
 * Uses the shared lottery form.
 * Publishing will be implemented with the backend later.
 */
@Composable
fun OwnerCreateLotteryScreen(
    onBackClick: () -> Unit,
    onPublishClick: (LotteryFormData) -> Unit = {}
) {
    LotteryForm(
        title = "Create Lottery",
        description = "Set up a lottery for your campsites. Choose who can enter and when winners can book.",
        actionLabel = "Publish Lottery",
        onBackClick = onBackClick,
        onSubmitClick = onPublishClick
    )
}
