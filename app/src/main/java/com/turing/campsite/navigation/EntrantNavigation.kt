package com.turing.campsite.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.turing.campsite.ui.screens.entrant.EntrantHomeScreen
import com.turing.campsite.ui.screens.entrant.EntrantCampsiteListScreen
import com.turing.campsite.ui.screens.entrant.EntrantSelectCampsiteScreen
import com.turing.campsite.ui.screens.entrant.EntrantQRCodeScreen
import com.turing.campsite.ui.screens.entrant.EntrantNotificationScreen
import com.turing.campsite.ui.screens.entrant.EntrantAcceptReservationScreen
import android.net.Uri
import com.turing.campsite.ui.screens.entrant.EntrantCampsiteDetailsScreen

/**
 * Registers all entrant navigation destinations.
 */
fun NavGraphBuilder.entrantGraph(
    navController: NavHostController
) {
    composable(Routes.ENTRANT_HOME) {
        EntrantHomeScreen(
            onCampsiteClick = { campsiteName ->
                navController.navigate(
                    "${Routes.ENTRANT_CAMPSITE_DETAILS}/${Uri.encode(campsiteName)}"
                )
            }
        )
    }

    composable(
        "${Routes.ENTRANT_CAMPSITE_DETAILS}/{campsiteName}"
    ) { backStackEntry ->

        val campsiteName =
            backStackEntry.arguments?.getString("campsiteName").orEmpty()

        EntrantCampsiteDetailsScreen(
            campsiteName = campsiteName,
            onBackClick = {
                navController.popBackStack()
            },
            onEnterLotteryClick = {
                // TODO: Connect to lottery entry flow.
            }
        )
    }

    composable(Routes.ENTRANT_CAMPSITE_LIST) {
        EntrantCampsiteListScreen()
    }

    composable(Routes.ENTRANT_SELECT_CAMPSITE) {
        EntrantSelectCampsiteScreen(navController)
    }

    composable(Routes.ENTRANT_QR_CODE) {
        EntrantQRCodeScreen()
    }

    composable(Routes.ENTRANT_NOTIFICATION) {
        EntrantNotificationScreen()
    }

    composable(Routes.ENTRANT_ACCEPT_RESERVATION) {
        EntrantAcceptReservationScreen(navController)
    }
}
