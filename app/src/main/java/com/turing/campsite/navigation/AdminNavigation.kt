package com.turing.campsite.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

import com.turing.campsite.ui.viewmodel.CampsiteViewModel
import com.turing.campsite.ui.viewmodel.UserViewModel

import com.turing.campsite.ui.screens.admin.AdminHomeScreen
import com.turing.campsite.ui.screens.admin.AdminProfileScreen
import com.turing.campsite.ui.screens.admin.AdminBrowseProfilesScreen
import com.turing.campsite.ui.screens.admin.AdminBrowseCampsiteScreen
import com.turing.campsite.ui.screens.admin.AdminMessageLogScreen
import com.turing.campsite.ui.screens.admin.AdminBrowseImageScreen
import com.turing.campsite.ui.screens.admin.AdminNotificationLogScreen
import com.turing.campsite.ui.screens.admin.AdminConversationHistoryScreen

/**
 * Registers all administrator navigation destinations.
 */
fun NavGraphBuilder.adminGraph(
    navController: NavHostController
) {
    navigation(
        route = Routes.ADMIN_GRAPH,
        startDestination = Routes.ADMIN_HOME
    ) {
        composable(Routes.ADMIN_HOME) {
            AdminHomeScreen(
                onSettingsClick = {
                    navController.navigate(Routes.ADMIN_PROFILE)
                },
                onCampsitesClick = {
                    navController.navigate(Routes.ADMIN_BROWSE_CAMPSITE)
                },
                onProfilesClick = {
                    navController.navigate(Routes.ADMIN_BROWSE_PROFILE)
                },
                onImagesClick = {
                    navController.navigate(Routes.ADMIN_BROWSE_IMAGE)
                },
                onMessageLogsClick = {
                    navController.navigate(Routes.ADMIN_MESSAGE_LOG)
                },
                onNotificationLogsClick = {
                    navController.navigate(Routes.ADMIN_NOTIFICATION_LOG)
                }
            )
        }

        // Administrator's own profile and settings
        composable(Routes.ADMIN_PROFILE) {
            AdminProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSwitchToOwnerClick = {
                    navController.switchView(Routes.OWNER_HOME)
                },
                onSwitchToEntrantClick = {
                    navController.switchView(Routes.ENTRANT_HOME)
                }
            )
        }

        // Browse all user profiles
        composable(Routes.ADMIN_BROWSE_PROFILE) {
            // Every user document is read from the database.
            backStackEntry ->
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ADMIN_GRAPH)
            }
            val userViewModel: UserViewModel = viewModel(parent)

            LaunchedEffect(Unit) {
                userViewModel.loadAll()
            }

            ErrorToast(userViewModel)

            AdminBrowseProfilesScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                profiles = userViewModel.users,
                isLoading = userViewModel.isLoading
            )
        }

        composable(Routes.ADMIN_BROWSE_CAMPSITE) {
            backStackEntry ->
            // Every campsite is read from the database, paused ones included.
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ADMIN_GRAPH)
            }
            val campsiteViewModel: CampsiteViewModel = viewModel(parent)

            LaunchedEffect(Unit) {
                campsiteViewModel.loadAll()
            }
            ErrorToast(campsiteViewModel)

            AdminBrowseCampsiteScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                campsites = campsiteViewModel.campsites,
                isLoading = campsiteViewModel.isLoading,
                onDeleteCampsite = { campsiteId ->
                    campsiteViewModel.delete(campsiteId)
                }
            )
        }

        composable(Routes.ADMIN_MESSAGE_LOG) {
            AdminMessageLogScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onConversationClick = { conversationId ->
                    navController.navigate(
                        "${Routes.ADMIN_CONVERSATION_HISTORY}/$conversationId"
                    )
                }
            )
        }

        composable(Routes.ADMIN_BROWSE_IMAGE) {
            AdminBrowseImageScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ADMIN_NOTIFICATION_LOG) {
            AdminNotificationLogScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("${Routes.ADMIN_CONVERSATION_HISTORY}/{conversationId}") {
            // TODO: load the messages of this conversation. Sample messages are shown for now.
            AdminConversationHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
