package com.turing.campsite.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import android.net.Uri

import com.turing.campsite.ui.viewmodel.CampsiteViewModel
import com.turing.campsite.ui.viewmodel.LotteryViewModel
import com.turing.campsite.ui.viewmodel.ReservationViewModel
import com.turing.campsite.ui.viewmodel.NotificationViewModel

import com.turing.campsite.ui.screens.entrant.EntrantHomeScreen
import com.turing.campsite.ui.screens.entrant.EntrantCampsiteListScreen
import com.turing.campsite.ui.screens.entrant.EntrantSelectCampsiteScreen
import com.turing.campsite.ui.screens.entrant.EntrantQRCodeScreen
import com.turing.campsite.ui.screens.entrant.EntrantNotificationScreen
import com.turing.campsite.ui.screens.entrant.EntrantAcceptReservationScreen
import com.turing.campsite.ui.screens.entrant.EntrantCampsiteDetailsScreen

/**
 * Registers all entrant navigation destinations.
 */
fun NavGraphBuilder.entrantGraph(
    navController: NavHostController
) {
    composable(Routes.ENTRANT_HOME) {
        val campsiteViewModel: CampsiteViewModel = viewModel()
        ErrorToast(campsiteViewModel)

        EntrantHomeScreen(
            campsiteViewModel = campsiteViewModel,
            onCampsiteClick = { campsiteId ->
                navController.navigate(
                    "${Routes.ENTRANT_CAMPSITE_DETAILS}/${Uri.encode(campsiteId)}"
                )
            }
        )
    }

    composable(
        "${Routes.ENTRANT_CAMPSITE_DETAILS}/{campsiteId}"
    ) { backStackEntry ->

        val campsiteId =
            backStackEntry.arguments?.getString("campsiteId").orEmpty()

        val campsiteViewModel: CampsiteViewModel = viewModel()
        val lotteryViewModel: LotteryViewModel = viewModel()

        EntrantCampsiteDetailsScreen(
            campsiteViewModel = campsiteViewModel,
            lotteryViewModel = lotteryViewModel,
            campsiteId = campsiteId,
            onBackClick = {
                navController.popBackStack()
            },
            onEnterLotteryClick = {
            }
        )
    }

    composable(Routes.ENTRANT_CAMPSITE_LIST) {
        val reservationViewModel: ReservationViewModel = viewModel()
        EntrantCampsiteListScreen(
            reservationViewModel = reservationViewModel
        )
    }

    composable(Routes.ENTRANT_SELECT_CAMPSITE) {
        EntrantSelectCampsiteScreen(navController)
    }

    composable(Routes.ENTRANT_QR_CODE) {
        EntrantQRCodeScreen()
    }

    composable(Routes.ENTRANT_NOTIFICATION) {
        val notificationViewModel: NotificationViewModel = viewModel()
        EntrantNotificationScreen(
            notificationViewModel = notificationViewModel
        )
    }

    composable(Routes.ENTRANT_ACCEPT_RESERVATION) {
        EntrantAcceptReservationScreen(navController)
    }
}