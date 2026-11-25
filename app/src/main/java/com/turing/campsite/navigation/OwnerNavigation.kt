package com.turing.campsite.navigation

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.turing.campsite.data.auth.DeviceAuthManager

import com.turing.campsite.data.auth.TempUser
import com.turing.campsite.ui.mapper.toCampsite
import com.turing.campsite.ui.viewmodel.CampsiteViewModel
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
import com.turing.campsite.ui.viewmodel.LotteryViewModel

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

            val campsiteViewModel: CampsiteViewModel = viewModel(parent)
            LaunchedEffect(Unit) {
                campsiteViewModel.listByOwner(TempUser.UID)
            }

            OwnerHomeScreen(
                deviceAuthManager = DeviceAuthManager(),
                campsiteViewModel = campsiteViewModel,
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

        composable("${Routes.OWNER_REGISTER_NEW_CAMPSITE}/{ownerId}")
        { backStackEntry ->
            val ownerId =
                backStackEntry.arguments?.getString("ownerId").orEmpty()
            val campsiteViewModel: CampsiteViewModel = viewModel()
            ErrorToast(campsiteViewModel)

            OwnerRegisterNewCampsiteScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPublishClick = { formData ->
                    // Ignore extra taps while the first save is still running.
                    if (!campsiteViewModel.isLoading) {
                        campsiteViewModel.add(formData.toCampsite(TempUser.UID)) {
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

        composable("${Routes.OWNER_CAMPSITE_FULL_DETAIL}/campsiteId") { backStackEntry ->

            val campsiteId =
                backStackEntry.arguments?.getString("campsiteId").orEmpty()

            val campsiteViewModel: CampsiteViewModel = viewModel()
            val lotteryViewModel: LotteryViewModel = viewModel()

            OwnerCampsiteFullDetailScreen(
                campsiteViewModel = campsiteViewModel,
                lotteryViewModel = lotteryViewModel,
                campsiteId = campsiteId,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(Routes.OWNER_EDIT_CAMPSITE)
                },
                onQRCodeClick = {
                    navController.navigate(Routes.OWNER_CAMPSITE_QR_CODE)
                },
                onEditLotteryClick = {
                    navController.navigate(Routes.OWNER_EDIT_LOTTERY)
                },
                onLotteryClick = {
                    navController.navigate(Routes.OWNER_LOTTERY_DETAILS)
                },
                onReservationsClick = {
                    navController.navigate(Routes.OWNER_CAMPSITE_HISTORY)
                }
            )
        }

        composable(Routes.OWNER_CAMPSITE_HISTORY) {
            OwnerCampsiteHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onMessageClick = { reservationId ->
                    navController.navigate(Routes.OWNER_MESSAGE)
                },
                onRejectClick = { reservationId ->
                    // TODO: Implement reservation rejection and refund.
                }
            )
        }

        composable(Routes.OWNER_MESSAGE) {
            OwnerMessageScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.OWNER_LOTTERY_DETAILS) {
            OwnerLotteryDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSendNotificationClick = {
                    navController.navigate(Routes.OWNER_NOTIFICATION)
                }
            )
        }

        // Create Lottery
        composable(Routes.OWNER_CREATE_LOTTERY) { backStackEntry ->
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry("ownerGraph")
            }
            val shared: CampsiteViewModel = viewModel(parent)

            OwnerCreateLotteryScreen(
                shared,
                deviceAuthManager = DeviceAuthManager(),

                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Lottery
        composable(Routes.OWNER_EDIT_LOTTERY) { backStackEntry ->
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry("ownerGraph")
            }
            val shared: CampsiteViewModel = viewModel(parent)
            OwnerEditLotteryScreen(
                shared,
                deviceAuthManager = DeviceAuthManager(),
                onBackClick = {
                    navController.popBackStack()
                }
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
