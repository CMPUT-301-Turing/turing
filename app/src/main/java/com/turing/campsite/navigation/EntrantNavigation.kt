package com.turing.campsite.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.turing.campsite.ui.mapper.toDetailsPreview
import com.turing.campsite.ui.viewmodel.CampsiteViewModel

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
        // All campsites are read from the database, only active ones are listed.
        val campsiteViewModel: CampsiteViewModel = viewModel()
        LaunchedEffect(Unit) {
            campsiteViewModel.loadAll()
        }
        ErrorToast(campsiteViewModel)

        EntrantHomeScreen(
            onCampsiteClick = { campsiteId ->
                navController.navigate(
                    "${Routes.ENTRANT_CAMPSITE_DETAILS}/${Uri.encode(campsiteId)}"
                )
            },
            campsites = campsiteViewModel.campsites.filter { it.active }
        )
    }

    composable(
        "${Routes.ENTRANT_CAMPSITE_DETAILS}/{campsiteId}"
    ) { backStackEntry ->

        val campsiteId =
            backStackEntry.arguments?.getString("campsiteId").orEmpty()

        val campsiteViewModel: CampsiteViewModel = viewModel()
        LaunchedEffect(campsiteId) {
            campsiteViewModel.select(campsiteId)
        }

        val campsite = campsiteViewModel.selectedCampsite

        if (campsite == null && campsiteViewModel.errorMessage == null) {
            // Still reading the campsite from the database.
            LoadingBox()
        } else {
            EntrantCampsiteDetailsScreen(
                campsiteName = campsite?.name.orEmpty(),
                onBackClick = {
                    navController.popBackStack()
                },
                onEnterLotteryClick = {
                    // TODO: Connect to lottery entry flow.
                },
                campsite = campsite?.toDetailsPreview()
            )
        }
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
