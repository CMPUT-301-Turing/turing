package com.turing.campsite.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.turing.campsite.ui.mapper.toProfilePreview
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
                navController.navigate(Routes.OWNER_HOME)
            },
            onSwitchToEntrantClick = {
                navController.navigate(Routes.ENTRANT_HOME)
            }
        )
    }

// Browse all user profiles
    composable(Routes.ADMIN_BROWSE_PROFILE) {
        // Every user document is read from the database.
        val userViewModel: UserViewModel = viewModel()
        LaunchedEffect(Unit) {
            userViewModel.loadAll()
        }
        ErrorToast(userViewModel)

        AdminBrowseProfilesScreen(
            onBackClick = {
                navController.popBackStack()
            },
            profiles = userViewModel.users.map { it.toProfilePreview() }
        )
    }

    composable(Routes.ADMIN_BROWSE_CAMPSITE) {
        // Every campsite is read from the database, paused ones included.
        val campsiteViewModel: CampsiteViewModel = viewModel()
        LaunchedEffect(Unit) {
            campsiteViewModel.loadAll()
        }
        ErrorToast(campsiteViewModel)

        AdminBrowseCampsiteScreen(
            onBackClick = {
                navController.popBackStack()
            },
            campsites = campsiteViewModel.campsites
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

    composable("${Routes.ADMIN_CONVERSATION_HISTORY}/{conversationId}") { backStackEntry ->
        val conversationId =
            backStackEntry.arguments?.getString("conversationId").orEmpty()

        AdminConversationHistoryScreen(
            conversationId = conversationId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}
