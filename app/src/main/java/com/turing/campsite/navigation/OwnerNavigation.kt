package com.turing.campsite.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.turing.campsite.ui.screens.owner.OwnerHomeScreen
import com.turing.campsite.ui.screens.owner.OwnerRegisterNewCampsiteScreen
import com.turing.campsite.ui.screens.owner.OwnerCampsiteFullDetailScreen
import com.turing.campsite.ui.screens.owner.OwnerCampsiteHistoryScreen
import com.turing.campsite.ui.screens.owner.OwnerMessageScreen
import com.turing.campsite.ui.screens.owner.OwnerLotteryDetailsScreen
import com.turing.campsite.ui.screens.owner.OwnerEditLotteryScreen
import com.turing.campsite.ui.screens.owner.OwnerCreateLotteryScreen
import com.turing.campsite.ui.screens.owner.OwnerCampsiteQRCodeScreen
import com.turing.campsite.ui.screens.owner.OwnerEditCampsiteScreen
import com.turing.campsite.ui.screens.owner.OwnerNotificationScreen

/**
 * Registers all campsite owner navigation destinations.
 */
fun NavGraphBuilder.ownerGraph(
    navController: NavHostController
) {
    composable(Routes.OWNER_HOME) {
        OwnerHomeScreen(navController)
    }

    composable(Routes.OWNER_REGISTER_NEW_CAMPSITE) {
        OwnerRegisterNewCampsiteScreen(navController)
    }

    composable(Routes.OWNER_CAMPSITE_FULL_DETAIL) {
        OwnerCampsiteFullDetailScreen(navController)
    }

    composable(Routes.OWNER_CAMPSITE_HISTORY) {
        OwnerCampsiteHistoryScreen(navController)
    }

    composable(Routes.OWNER_MESSAGE) {
        OwnerMessageScreen(navController)
    }

    composable(Routes.OWNER_LOTTERY_DETAILS) {
        OwnerLotteryDetailsScreen(navController)
    }

    composable(Routes.OWNER_EDIT_LOTTERY) {
        OwnerEditLotteryScreen(navController)
    }

    composable(Routes.OWNER_CREATE_LOTTERY) {
        OwnerCreateLotteryScreen(navController)
    }

    composable(Routes.OWNER_CAMPSITE_QR_CODE) {
        OwnerCampsiteQRCodeScreen(navController)
    }

    composable(Routes.OWNER_EDIT_CAMPSITE) {
        OwnerEditCampsiteScreen(navController)
    }

    composable(Routes.OWNER_NOTIFICATION) {
        OwnerNotificationScreen(navController)
    }
}
