/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */
package com.turing.campsite.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.turing.campsite.data.model.UserRole
import androidx.navigation.NavGraphBuilder
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
    roleComposable(requiredRole = UserRole.ENTRANT, navController = navController, route = Routes.ENTRANT_HOME) {
        val campsiteViewModel: CampsiteViewModel = viewModel()
        val lotteryViewModel: LotteryViewModel = viewModel()
        ErrorToast(campsiteViewModel)

        EntrantHomeScreen(
            campsiteViewModel = campsiteViewModel,
            lotteryViewModel =  lotteryViewModel,
        ) { campsiteId ->
            navController.navigate(
                "${Routes.ENTRANT_CAMPSITE_DETAILS}/${Uri.encode(campsiteId)}"
            )
        }
    }

    roleComposable(requiredRole = UserRole.ENTRANT, navController = navController, route =
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

    roleComposable(requiredRole = UserRole.ENTRANT, navController = navController, route = Routes.ENTRANT_CAMPSITE_LIST) {
        val reservationViewModel: ReservationViewModel = viewModel()
        EntrantCampsiteListScreen(
            reservationViewModel = reservationViewModel
        )
    }

    roleComposable(requiredRole = UserRole.ENTRANT, navController = navController, route = Routes.ENTRANT_SELECT_CAMPSITE) {
        EntrantSelectCampsiteScreen(navController)
    }

    roleComposable(requiredRole = UserRole.ENTRANT, navController = navController, route = Routes.ENTRANT_QR_CODE) {
        EntrantQRCodeScreen()
    }

    roleComposable(requiredRole = UserRole.ENTRANT, navController = navController, route = Routes.ENTRANT_NOTIFICATION) {
        val notificationViewModel: NotificationViewModel = viewModel()
        EntrantNotificationScreen(
            notificationViewModel = notificationViewModel
        )
    }

    roleComposable(requiredRole = UserRole.ENTRANT, navController = navController, route = Routes.ENTRANT_ACCEPT_RESERVATION) {
        EntrantAcceptReservationScreen(navController)
    }
}