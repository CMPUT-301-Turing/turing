package com.turing.campsite.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import com.turing.campsite.ui.screens.admin.AdminHomeScreen
import com.turing.campsite.ui.screens.admin.AdminProfileScreen
import com.turing.campsite.ui.screens.admin.AdminProfileSettingScreen
import com.turing.campsite.ui.screens.admin.AdminCampsiteScreen
import com.turing.campsite.ui.screens.admin.AdminMessageLogScreen
import com.turing.campsite.ui.screens.admin.AdminBrowseImageScreen
import com.turing.campsite.ui.screens.admin.AdminNotificationLogScreen
import com.turing.campsite.ui.screens.admin.AdminConversationScreen

/**
 * Registers all administrator navigation destinations.
 */
fun NavGraphBuilder.adminGraph(
    navController: NavHostController
) {
    composable(Routes.ADMIN_HOME) {
        AdminHomeScreen(
            onSettingsClick = {
                navController.navigate(Routes.ADMIN_PROFILE_SETTING)
            },
            onCampsitesClick = {
                navController.navigate(Routes.ADMIN_CAMPSITE)
            },
            onProfilesClick = {
                navController.navigate(Routes.ADMIN_PROFILE)
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

    composable(Routes.ADMIN_PROFILE) {
        AdminProfileScreen(navController)
    }

    composable(Routes.ADMIN_PROFILE_SETTING) {
        AdminProfileSettingScreen(navController)
    }

    composable(Routes.ADMIN_CAMPSITE) {
        AdminCampsiteScreen(navController)
    }

    composable(Routes.ADMIN_MESSAGE_LOG) {
        AdminMessageLogScreen(navController)
    }

    composable(Routes.ADMIN_BROWSE_IMAGE) {
        AdminBrowseImageScreen(navController)
    }

    composable(Routes.ADMIN_NOTIFICATION_LOG) {
        AdminNotificationLogScreen(navController)
    }

    composable(Routes.ADMIN_CONVERSATION) {
        AdminConversationScreen(navController)
    }
}
