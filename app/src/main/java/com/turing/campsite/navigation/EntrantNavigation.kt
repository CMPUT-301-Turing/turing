package com.turing.campsite.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.turing.campsite.ui.screens.entrant.EntrantHomeScreen
import com.turing.campsite.ui.screens.entrant.EntrantProfileScreen
import com.turing.campsite.ui.screens.entrant.EntrantEditProfileScreen
import com.turing.campsite.ui.screens.entrant.EntrantCampsiteListScreen
import com.turing.campsite.ui.screens.entrant.EntrantSelectCampsiteScreen
import com.turing.campsite.ui.screens.entrant.EntrantQRCodeScreen
import com.turing.campsite.ui.screens.entrant.EntrantNotificationScreen
import com.turing.campsite.ui.screens.entrant.EntrantAcceptReservationScreen

/**
 * Registers all entrant navigation destinations.
 */
fun NavGraphBuilder.entrantGraph(
    navController: NavHostController
) {
    composable(Routes.ENTRANT_HOME) {
        EntrantHomeScreen()
    }

    composable(Routes.ENTRANT_PROFILE) {
        EntrantProfileScreen(
            onEditProfileClick = {
                navController.navigate(Routes.ENTRANT_EDIT_PROFILE)
            }
        )
    }

    composable(Routes.ENTRANT_EDIT_PROFILE) {
        EntrantEditProfileScreen(
            onBackClick = {
                navController.popBackStack()
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
