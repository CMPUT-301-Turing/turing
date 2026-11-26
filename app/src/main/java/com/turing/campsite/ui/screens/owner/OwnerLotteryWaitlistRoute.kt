package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turing.campsite.ui.screens.shared.section6Uid
import com.turing.campsite.ui.viewmodel.OwnerLotteryWaitlistViewModel

/**
 * Loads the read-only waitlist for one campsite. Route state is keyed by account and campsite.
 */
@Composable
internal fun OwnerLotteryWaitlistRoute(campsiteId: String, onBackClick: () -> Unit) {
    val uid = section6Uid()
    if (uid == null) {
        OwnerLotteryDetailsScreen(
            onBackClick = onBackClick,
            statusMessage = "Sign in with the campsite's owner account to view its waitlist.",
            readOnly = true
        )
        return
    }

    val model: OwnerLotteryWaitlistViewModel = viewModel(key = "waitlist:$uid:$campsiteId")
    LaunchedEffect(model, uid, campsiteId) { model.load(uid, campsiteId) }

    val status = when {
        model.isLoading -> "Loading waitlist."
        model.errorMessage != null -> "Could not load the waitlist. Reopen this screen to retry."
        else -> model.statusMessage
    }
    OwnerLotteryDetailsScreen(
        onBackClick = onBackClick,
        lottery = model.lottery,
        entries = model.entries,
        statusMessage = status,
        readOnly = true,
        campsiteName = model.campsite?.name
    )
}
