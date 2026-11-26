
package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.ui.components.LotteryForm
import com.turing.campsite.ui.components.LotteryFormData
import com.turing.campsite.ui.components.LotteryFormMode
import com.turing.campsite.ui.viewmodel.LotteryViewModel

/**
 * Screen for creating a new campsite lottery.
 *
 * Uses the shared lottery form.
 * Publishing will be implemented with the backend later.
 */

@Composable
fun OwnerCreateLotteryScreen(
    lotteryViewModel: LotteryViewModel,
    campsites: List<Campsite> = emptyList(),
    onBackClick: () -> Unit,
    onPublishClick: (LotteryFormData) -> Unit = {}

) {
    LotteryForm(
        lotteryViewModel = lotteryViewModel,
        mode = LotteryFormMode.CREATE,
        campsites = campsites,
        title = "Create Lottery",
        description = "Set up a lottery for your campsites. Choose who can enter and when winners can book.",
        actionLabel = "Publish Lottery",
        onBackClick = onBackClick,
        onSubmitClick = onPublishClick
    )
}
