package com.turing.campsite.navigation

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.turing.campsite.ui.screens.shared.section6Uid
import com.turing.campsite.ui.mapper.toCampsite
import com.turing.campsite.ui.viewmodel.CampsiteViewModel
import com.turing.campsite.ui.screens.owner.OwnerHomeScreen
import com.turing.campsite.ui.screens.owner.OwnerRegisterNewCampsiteScreen
import com.turing.campsite.ui.screens.owner.OwnerCampsiteFullDetailScreen
import com.turing.campsite.ui.screens.owner.OwnerCampsiteHistoryRoute
import com.turing.campsite.ui.screens.owner.OwnerMessageScreen
import com.turing.campsite.ui.screens.owner.OwnerLotteryWaitlistRoute
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
    navigation(
        route = "ownerGraph",
        startDestination = Routes.OWNER_HOME
    ) {
        composable(Routes.OWNER_HOME) { backStackEntry ->
            // The owner's campsites are read from the database every time this screen opens.
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry("ownerGraph")
            }

            // Keep the entry list and Section 6 routes on the same authenticated account.
            val ownerId = section6Uid()
            val campsiteViewModel: CampsiteViewModel = viewModel(parent, key = "owner-campsites:$ownerId")

            LaunchedEffect(backStackEntry, ownerId) {
                if (ownerId != null) campsiteViewModel.listByOwner(ownerId)
            }

            OwnerHomeScreen(
                campsites = campsiteViewModel.ownerCampsites.filter { it.ownerId == ownerId },
                canManageCampsites = ownerId != null,
                campsiteStatus = when {
                    ownerId == null -> "Sign in before managing campsites."
                    campsiteViewModel.isLoading -> "Loading your campsites…"
                    campsiteViewModel.errorMessage != null -> "Could not load your campsites. Reopen Owner Home to retry."
                    campsiteViewModel.ownerCampsites.isEmpty() -> "No campsites are linked to this account yet."
                    else -> null
                },
                onNotificationsClick = {
                    navController.navigate(Routes.OWNER_NOTIFICATION)
                },
                onProfileClick = {
                    navController.navigate(Routes.OWNER_PROFILE)
                },
                onRegisterCampsiteClick = {
                    navController.navigate(Routes.OWNER_REGISTER_NEW_CAMPSITE)
                },
                onCreateLotteryClick = {
                    navController.navigate(Routes.OWNER_CREATE_LOTTERY)
                },
                onCampsiteClick = { campsiteId ->
                    navController.navigate(
                        "${Routes.OWNER_CAMPSITE_FULL_DETAIL}/${
                            Uri.encode(
                                campsiteId
                            )
                        }"
                    )
                },
            )
        }

        composable(Routes.OWNER_REGISTER_NEW_CAMPSITE) { backStackEntry ->
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry("ownerGraph")
            }
            val ownerId = section6Uid()
            val shared: CampsiteViewModel = viewModel(parent, key = "owner-campsites:$ownerId")
            ErrorToast(shared)
            OwnerRegisterNewCampsiteScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPublishClick = { formData ->

                    // Refuse saves from a stale screen after the signed-in account changes.
                    if (ownerId != null && Firebase.auth.currentUser?.uid == ownerId && !shared.isLoading) {
                        shared.add(formData.toCampsite(ownerId)) {
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

        composable(Routes.OWNER_EDIT_CAMPSITE) {
            OwnerEditCampsiteScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("${Routes.OWNER_CAMPSITE_FULL_DETAIL}/{campsiteId}") { backStackEntry ->
            val campsiteId =
                backStackEntry.arguments?.getString("campsiteId").orEmpty()

            val parent = remember(backStackEntry) {
                navController.getBackStackEntry("ownerGraph")
            }
            val ownerId = section6Uid()
            val shared: CampsiteViewModel = viewModel(parent, key = "owner-campsites:$ownerId")

            OwnerCampsiteFullDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(Routes.OWNER_EDIT_CAMPSITE)
                },
                onQRCodeClick = {
                    navController.navigate(Routes.OWNER_CAMPSITE_QR_CODE)
                },
                onLotteryClick = {
                    val route = Routes.OWNER_LOTTERY_DETAILS
                    navController.navigate("$route?campsiteId=${Uri.encode(campsiteId)}")
                },
                onReservationsClick = {
                    navController.navigate("${Routes.OWNER_CAMPSITE_HISTORY}?campsiteId=${Uri.encode(campsiteId)}")
                },
                campsiteViewModel = shared,
                campsiteId = campsiteId,
                ownerId = ownerId,
            )
        }

        composable(
            route = "${Routes.OWNER_CAMPSITE_HISTORY}?campsiteId={campsiteId}",
            arguments = listOf(navArgument("campsiteId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            OwnerCampsiteHistoryRoute(
                campsiteId = entry.arguments?.getString("campsiteId")?.takeIf { it.isNotBlank() },
                onBackClick = { navController.popBackStack() },
                onMessageClick = { navController.navigate(Routes.OWNER_MESSAGE) }
            )
        }

        composable(Routes.OWNER_MESSAGE) {
            OwnerMessageScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Routes.OWNER_LOTTERY_DETAILS}?campsiteId={campsiteId}",
            arguments = listOf(navArgument("campsiteId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            OwnerLotteryWaitlistRoute(
                campsiteId = entry.arguments?.getString("campsiteId").orEmpty(),
                onBackClick = { navController.popBackStack() }
            )
        }

        // Create Lottery
        composable(Routes.OWNER_CREATE_LOTTERY) { backStackEntry ->
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry("ownerGraph")
            }
            val ownerId = section6Uid()
            val shared: CampsiteViewModel = viewModel(parent, key = "owner-campsites:$ownerId")

            OwnerCreateLotteryScreen(
                campsites = shared.ownerCampsites.filter { it.ownerId == ownerId },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.OWNER_CAMPSITE_QR_CODE) {
            OwnerCampsiteQRCodeScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.OWNER_NOTIFICATION) {
            OwnerNotificationScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
