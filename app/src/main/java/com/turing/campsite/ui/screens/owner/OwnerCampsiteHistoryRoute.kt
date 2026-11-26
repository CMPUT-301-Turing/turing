package com.turing.campsite.ui.screens.owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.turing.campsite.data.model.ReservationStatus
import com.turing.campsite.data.model.reservationDayStart
import com.turing.campsite.data.model.statusForOwnerHistory
import com.turing.campsite.ui.screens.shared.section6Uid
import com.turing.campsite.ui.viewmodel.ReservationViewModel

/**
 * Connects the history screen to the authenticated owner's reservation query.
 * When no campsite is selected, records from deleted campsites can still appear in history.
 *
 * @param campsiteId optional campsite filter; null shows all records for this owner
 */
@Composable
fun OwnerCampsiteHistoryRoute(
    onBackClick: () -> Unit,
    onMessageClick: (String) -> Unit,
    campsiteId: String? = null
) {
    val uid = section6Uid()
    if (uid == null) {
        OwnerCampsiteHistoryScreen(
            onBackClick = onBackClick,
            onMessageClick = onMessageClick,
            onRejectClick = {},
            reservations = emptyList(),
            statusMessage = "Sign in to see your reservations."
        )
        return
    }

    key(uid) {
        val reservationsViewModel: ReservationViewModel = viewModel(key = "owner-reservations:$uid")
        LaunchedEffect(uid) { reservationsViewModel.loadForOwner(uid) }

        val ownedReservations = reservationsViewModel.reservations.ownedBy(uid)
            .filter { campsiteId == null || it.campsiteId == campsiteId }
        OwnerCampsiteHistoryScreen(
            onBackClick = onBackClick,
            onMessageClick = onMessageClick,
            onRejectClick = { reservationId ->
                val dayStart = reservationDayStart()
                if (FirebaseAuth.getInstance().currentUser?.uid == uid &&
                    !reservationsViewModel.isLoading &&
                    ownedReservations.any {
                        it.id == reservationId &&
                            it.statusForOwnerHistory(dayStart) == ReservationStatus.ACTIVE
                    }
                ) {
                    reservationsViewModel.rejectOwned(reservationId, uid)
                }
            },
            reservations = ownedReservations,
            isLoading = reservationsViewModel.isLoading,
            statusMessage = reservationsViewModel.errorMessage?.let {
                "Couldn’t load or update reservations. Reopen this screen to retry."
            }
        )
    }
}
